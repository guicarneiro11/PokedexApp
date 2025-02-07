package com.guicarneirodev.pokedexapp.core.network.models

import com.google.gson.annotations.SerializedName
import com.guicarneirodev.pokedexapp.core.domain.model.Ability
import com.guicarneirodev.pokedexapp.core.domain.model.Move
import com.guicarneirodev.pokedexapp.core.domain.model.PokemonDetails
import com.guicarneirodev.pokedexapp.core.domain.model.Stat

data class PokemonDetailsResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeResponse>,
    val stats: List<StatResponse>,
    val sprites: SpritesResponse,
    val abilities: List<AbilityResponse>,
    val moves: List<MoveResponse>
) {
    fun toPokemonDetails() = PokemonDetails(
        id = id,
        name = name,
        height = height / 10.0, // convert to meters
        weight = weight / 10.0, // convert to kg
        types = types.map { it.type.name },
        stats = stats.map { Stat(it.stat.name, it.baseStat) },
        imageUrl = sprites.frontDefault,
        abilities = abilities.map {
            Ability(
                name = it.ability.name.replace("-", " "),
                isHidden = it.isHidden
            )
        },
        moves = moves.mapNotNull { moveResponse ->
            // A URL do move tem o formato: "https://pokeapi.co/api/v2/move/{id}/"
            val moveType = moveResponse.move.url.split("/").dropLast(1).last()
            Move(
                name = moveResponse.move.name.replace("-", " "),
                type = moveType
            )
        }
    )
}
