package com.guicarneirodev.pokedexapp.core.network.models

data class PokemonResult(
    val name: String,
    val url: String
) {
    val id: Int get() = url.split("/").dropLast(1).last().toInt()
}