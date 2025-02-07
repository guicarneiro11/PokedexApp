package com.guicarneirodev.pokedexapp.features.details.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun PokemonImage(
    imageUrl: String,
    pokemonName: String,
    modifier: Modifier = Modifier,
    iconSize: Dp = 48.dp
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = pokemonName,
            modifier = Modifier.fillMaxSize(),
            onSuccess = { isLoading = false },
            onError = {
                isLoading = false
                isError = true
            }
        )

        if (isLoading) {
            LoadingIndicator(iconSize)
        }

        if (isError) {
            ErrorIcon(iconSize)
        }
    }
}

@Composable
private fun LoadingIndicator(size: Dp) {
    CircularProgressIndicator(
        modifier = Modifier.size(size)
    )
}

@Composable
private fun ErrorIcon(size: Dp) {
    Icon(
        imageVector = Icons.Default.Error,
        contentDescription = null,
        modifier = Modifier.size(size),
        tint = MaterialTheme.colorScheme.error
    )
}