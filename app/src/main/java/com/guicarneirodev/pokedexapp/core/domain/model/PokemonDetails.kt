package com.guicarneirodev.pokedexapp.core.domain.model

data class PokemonDetails(
    val id: Int,
    val name: String,
    val height: Double,
    val weight: Double,
    val types: List<String>,
    val stats: List<Stat>,
    val imageUrl: String
)