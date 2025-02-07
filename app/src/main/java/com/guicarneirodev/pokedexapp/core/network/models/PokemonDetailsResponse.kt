package com.guicarneirodev.pokedexapp.core.network.models

import com.guicarneirodev.pokedexapp.core.domain.model.PokemonDetails
import com.guicarneirodev.pokedexapp.core.domain.model.Stat

data class PokemonDetailsResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeResponse>,
    val stats: List<StatResponse>,
    val sprites: SpritesResponse
) {
    fun toPokemonDetails() = PokemonDetails(
        id = id,
        name = name,
        height = height / 10.0, // convert to meters
        weight = weight / 10.0, // convert to kg
        types = types.map { it.type.name },
        stats = stats.map { Stat(it.stat.name, it.baseStat) },
        imageUrl = sprites.frontDefault
    )
}