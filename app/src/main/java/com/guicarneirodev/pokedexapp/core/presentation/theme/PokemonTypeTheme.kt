package com.guicarneirodev.pokedexapp.core.presentation.theme

import androidx.compose.ui.graphics.Color

object PokemonTypeColor {
    val Normal = Color(0xFFA8A878)
    val Fire = Color(0xFFF08030)
    val Water = Color(0xFF6890F0)
    val Electric = Color(0xFFF8D030)
    val Grass = Color(0xFF78C850)
    val Ice = Color(0xFF98D8D8)
    val Fighting = Color(0xFFC03028)
    val Poison = Color(0xFFA040A0)
    val Ground = Color(0xFFE0C068)
    val Flying = Color(0xFFA890F0)
    val Psychic = Color(0xFFF85888)
    val Bug = Color(0xFFA8B820)
    val Rock = Color(0xFFB8A038)
    val Ghost = Color(0xFF705898)
    val Dragon = Color(0xFF7038F8)
    val Dark = Color(0xFF705848)
    val Steel = Color(0xFFB8B8D0)
    val Fairy = Color(0xFFEE99AC)
}

fun getPokemonTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "normal" -> PokemonTypeColor.Normal
        "fire" -> PokemonTypeColor.Fire
        "water" -> PokemonTypeColor.Water
        "electric" -> PokemonTypeColor.Electric
        "grass" -> PokemonTypeColor.Grass
        "ice" -> PokemonTypeColor.Ice
        "fighting" -> PokemonTypeColor.Fighting
        "poison" -> PokemonTypeColor.Poison
        "ground" -> PokemonTypeColor.Ground
        "flying" -> PokemonTypeColor.Flying
        "psychic" -> PokemonTypeColor.Psychic
        "bug" -> PokemonTypeColor.Bug
        "rock" -> PokemonTypeColor.Rock
        "ghost" -> PokemonTypeColor.Ghost
        "dragon" -> PokemonTypeColor.Dragon
        "dark" -> PokemonTypeColor.Dark
        "steel" -> PokemonTypeColor.Steel
        "fairy" -> PokemonTypeColor.Fairy
        else -> Color.Gray
    }
}