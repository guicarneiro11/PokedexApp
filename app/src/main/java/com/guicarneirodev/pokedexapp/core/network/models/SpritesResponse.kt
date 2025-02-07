package com.guicarneirodev.pokedexapp.core.network.models

import com.google.gson.annotations.SerializedName

data class SpritesResponse(
    @SerializedName("front_default") val frontDefault: String
)