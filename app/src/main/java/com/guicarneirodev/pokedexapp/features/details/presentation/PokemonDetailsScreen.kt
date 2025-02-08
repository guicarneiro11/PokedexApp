package com.guicarneirodev.pokedexapp.features.details.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.guicarneirodev.pokedexapp.core.network.models.UiState
import com.guicarneirodev.pokedexapp.features.details.presentation.components.PokemonDetailsContent
import com.guicarneirodev.pokedexapp.features.shared.error.ErrorView
import com.guicarneirodev.pokedexapp.features.shared.loading.LoadingView
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailsScreen(
    pokemonId: Int,
    viewModel: PokemonDetailsViewModel = koinViewModel(),
    onBackClick: () -> Unit
) {
    LaunchedEffect(pokemonId) {
        viewModel.loadPokemonDetails(pokemonId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pokémon Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> LoadingView()
                is UiState.Error -> ErrorView(
                    error = state.error,
                    onRetry = { viewModel.loadPokemonDetails(pokemonId) }
                )
                is UiState.Success -> PokemonDetailsContent(
                    pokemon = state.data,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}