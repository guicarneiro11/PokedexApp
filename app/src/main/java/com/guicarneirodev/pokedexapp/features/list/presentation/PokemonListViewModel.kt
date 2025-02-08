package com.guicarneirodev.pokedexapp.features.list.presentation

import android.database.sqlite.SQLiteException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.domain.error.toAppError
import com.guicarneirodev.pokedexapp.core.domain.repository.PokemonRepository
import com.guicarneirodev.pokedexapp.core.presentation.util.PokemonMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class PokemonListViewModel(
    private val repository: PokemonRepository,
    private val cache: PokemonMemoryCache
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    private val _prefetchError = MutableStateFlow<AppError?>(null)
    val prefetchError = _prefetchError.asStateFlow()

    private var loadJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            prefetchInitialData()
        }
    }

    val pokemonList = searchQuery
        .debounce(300L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                repository.getPokemonList()
            } else {
                repository.searchPokemon(query)
            }
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query.trim().lowercase()
    }

    fun prefetchInitialData() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                _isLoading.value = true
                _prefetchError.value = null

                repository.getPokemonList()
                    .catch { throwable ->
                        val error = when (throwable) {
                            is UnknownHostException -> AppError.NoInternetConnection("Check your internet connection")
                            is SocketTimeoutException -> AppError.Timeout("Request timed out")
                            is HttpException -> when (throwable.code()) {
                                in 500..599 -> AppError.Server("Server error, try again later")
                                else -> throwable.toAppError()
                            }
                            is SQLiteException -> AppError.Database("Database error")
                            else -> AppError.Unknown("Unknown error occurred")
                        }
                        _prefetchError.value = error
                    }
                    .collect { pagingData ->
                        pagingData.map { pokemon ->
                            cache.cachePokemon(pokemon.id, pokemon)
                        }
                    }
            } catch (e: Exception) {
                _prefetchError.value = when (e) {
                    is CancellationException -> throw e
                    else -> AppError.Unknown("Unexpected error during prefetch")
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retryPrefetch() {
        prefetchInitialData()
    }

    override fun onCleared() {
        super.onCleared()
        loadJob?.cancel()
    }
}