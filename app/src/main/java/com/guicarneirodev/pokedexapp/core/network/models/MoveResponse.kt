package com.guicarneirodev.pokedexapp.core.network.models

import com.google.gson.annotations.SerializedName

data class MoveResponse(
    @SerializedName("move") val move: NameUrlResponse,
    @SerializedName("type") val type: TypeResponse
)

