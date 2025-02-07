package com.guicarneirodev.pokedexapp.features.details.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.domain.model.PokemonDetails
import com.guicarneirodev.pokedexapp.core.domain.repository.PokemonRepository
import com.guicarneirodev.pokedexapp.core.network.models.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonDetailsViewModel(
    private val repository: PokemonRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<PokemonDetails>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadPokemonDetails(id: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val details = repository.getPokemonDetails(id)
                _uiState.value = UiState.Success(details)
            } catch (e: Exception) {
                val error = when (e) {
                    is AppError -> e
                    else -> AppError.Unknown(e.message)
                }
                _uiState.value = UiState.Error(error)
            }
        }
    }
}