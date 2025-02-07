package com.guicarneirodev.pokedexapp.core.presentation.components.pokemon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color

@Composable
fun PokemonTypeIcon(type: String, modifier: Modifier = Modifier) {
    val icon = when (type.lowercase()) {
        "normal" -> Icons.Default.Star
        "fire" -> Icons.Default.LocalFireDepartment
        "water" -> Icons.Default.WaterDrop
        "electric" -> Icons.Default.ElectricBolt
        "grass" -> Icons.Default.Grass
        "ice" -> Icons.Default.AcUnit
        "fighting" -> Icons.Default.FrontHand
        "poison" -> Icons.Default.Science
        "ground" -> Icons.Default.Landscape
        "flying" -> Icons.Default.Air
        "psychic" -> Icons.Default.Visibility
        "bug" -> Icons.Default.BugReport
        "rock" -> Icons.Default.Terrain
        "ghost" -> Icons.Default.BlurOn
        "dragon" -> Icons.Default.Cyclone
        "dark" -> Icons.Default.DarkMode
        "steel" -> Icons.Default.Link
        "fairy" -> Icons.Default.Stars
        else -> Icons.AutoMirrored.Filled.Help
    }

    Icon(
        imageVector = icon,
        contentDescription = "$type type",
        modifier = modifier,
        tint = Color.White
    )
}