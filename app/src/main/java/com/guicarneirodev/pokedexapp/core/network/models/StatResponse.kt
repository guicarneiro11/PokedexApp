package com.guicarneirodev.pokedexapp.core.network.models

import com.google.gson.annotations.SerializedName

data class StatResponse(
    @SerializedName("base_stat") val baseStat: Int,
    val stat: Stat
) {
    data class Stat(val name: String)
}