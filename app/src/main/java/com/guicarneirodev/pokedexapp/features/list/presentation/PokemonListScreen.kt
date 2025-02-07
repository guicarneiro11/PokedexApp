package com.guicarneirodev.pokedexapp.features.list.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.features.details.presentation.components.ErrorView
import com.guicarneirodev.pokedexapp.features.details.presentation.components.LoadingView
import com.guicarneirodev.pokedexapp.features.list.presentation.components.EmptySearchResult
import com.guicarneirodev.pokedexapp.features.list.presentation.components.LoadingItem
import com.guicarneirodev.pokedexapp.features.list.presentation.components.PokemonItem
import com.guicarneirodev.pokedexapp.features.list.presentation.components.PokemonSearchBar
import com.guicarneirodev.pokedexapp.features.list.presentation.components.PokemonTopBar
import org.koin.androidx.compose.koinViewModel

@Composable
fun PokemonListScreen(
    viewModel: PokemonListViewModel = koinViewModel(),
    onPokemonClick: (Int) -> Unit,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val pokemonPagingState = viewModel.pokemonList.collectAsLazyPagingItems()
    val query by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            Column {
                PokemonTopBar(
                    query = query,
                    onQueryChange = viewModel::onSearchQueryChange,
                    darkTheme = darkTheme,
                    onThemeChange = onThemeChange
                )
                PokemonSearchBar(
                    query = query,
                    onQueryChange = viewModel::onSearchQueryChange
                )
            }
        },
        modifier = Modifier.systemBarsPadding()
    ) { padding ->
        when {
            pokemonPagingState.loadState.refresh is LoadState.Loading -> {
                LoadingView()
            }
            pokemonPagingState.loadState.refresh is LoadState.Error -> {
                val error = (pokemonPagingState.loadState.refresh as LoadState.Error)
                    .error as? AppError ?: AppError.Unknown()
                ErrorView(
                    error = error,
                    onRetry = { pokemonPagingState.retry() }
                )
            }
            pokemonPagingState.itemCount == 0 && query.isNotEmpty() -> {
                EmptySearchResult(
                    query = query,
                    onClearSearch = { viewModel.onSearchQueryChange("") }
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        count = pokemonPagingState.itemCount,
                        key = { index ->
                            pokemonPagingState[index]?.let {
                                "pokemon_${it.id}_$index"
                            } ?: "empty_$index"
                        }
                    ) { index ->
                        pokemonPagingState[index]?.let { pokemon ->
                            PokemonItem(
                                pokemon = pokemon,
                                onClick = { onPokemonClick(pokemon.id) }
                            )
                        }
                    }

                    item {
                        when (val state = pokemonPagingState.loadState.append) {
                            is LoadState.Loading -> {
                                LoadingItem()
                            }
                            is LoadState.Error -> {
                                val error = state.error as? AppError ?: AppError.Unknown()
                                ErrorView(
                                    error = error,
                                    onRetry = { pokemonPagingState.retry() }
                                )
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}