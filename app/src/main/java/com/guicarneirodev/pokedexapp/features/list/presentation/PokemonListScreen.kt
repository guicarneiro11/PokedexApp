package com.guicarneirodev.pokedexapp.features.list.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.features.shared.error.ErrorView
import com.guicarneirodev.pokedexapp.features.list.presentation.components.EmptySearchResult
import com.guicarneirodev.pokedexapp.features.list.presentation.components.ErrorItem
import com.guicarneirodev.pokedexapp.features.list.presentation.components.PokemonItem
import com.guicarneirodev.pokedexapp.features.list.presentation.components.PokemonItemPlaceholder
import com.guicarneirodev.pokedexapp.features.list.presentation.components.PokemonSearchBar
import com.guicarneirodev.pokedexapp.features.list.presentation.components.PokemonTopBar
import kotlinx.coroutines.launch
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
    val prefetchError by viewModel.prefetchError.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.prefetchInitialData()
    }

    Scaffold(
        topBar = {
            Column {
                PokemonTopBar(
                    darkTheme = darkTheme,
                    onThemeChange = onThemeChange
                )
                PokemonSearchBar(
                    query = query,
                    onQueryChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = listState.firstVisibleItemIndex > 0,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Scroll to top"
                    )
                }
            }
        },
        modifier = Modifier.systemBarsPadding()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                prefetchError != null -> {
                    ErrorView(
                        error = prefetchError!!,
                        onRetry = { viewModel.retryPrefetch() }
                    )
                }

                pokemonPagingState.loadState.refresh is LoadState.Error -> {
                    val error = (pokemonPagingState.loadState.refresh as LoadState.Error)
                        .error as? AppError ?: AppError.Unknown()
                    ErrorView(
                        error = error,
                        onRetry = { pokemonPagingState.retry() }
                    )
                }

                pokemonPagingState.loadState.refresh is LoadState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                pokemonPagingState.itemCount == 0 && query.isNotEmpty() -> {
                    EmptySearchResult(
                        query = query,
                        onClearSearch = { viewModel.onSearchQueryChange("") }
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 8.dp
                        )
                    ) {
                        items(
                            count = pokemonPagingState.itemCount,
                            key = { index ->
                                val pokemon = pokemonPagingState[index]
                                pokemon?.id?.toString() ?: "loading_$index"
                            }
                        ) { index ->
                            val pokemon = pokemonPagingState[index]
                            if (pokemon != null) {
                                PokemonItem(
                                    pokemon = pokemon,
                                    onClick = { onPokemonClick(pokemon.id) }
                                )
                            } else {
                                PokemonItemPlaceholder()
                            }
                        }

                        when (val appendState = pokemonPagingState.loadState.append) {
                            is LoadState.Loading -> {
                                item(key = "loading") {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }
                            is LoadState.Error -> {
                                item(key = "error") {
                                    val error = appendState.error as? AppError ?: AppError.Unknown()
                                    ErrorItem(
                                        error = error,
                                        onRetry = { pokemonPagingState.retry() }
                                    )
                                }
                            }
                            else -> Unit
                        }
                    }
                }
            }
        }
    }
}