package com.guicarneirodev.pokedexapp.features.shared.image

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun PokemonImage(
    imageUrl: String,
    pokemonName: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = pokemonName,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}