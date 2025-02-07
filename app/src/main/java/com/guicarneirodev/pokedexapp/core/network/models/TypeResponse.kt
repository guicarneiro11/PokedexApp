package com.guicarneirodev.pokedexapp.core.network.models

import com.google.gson.annotations.SerializedName

data class TypeResponse(
    @SerializedName("type") val type: Type
) {
    data class Type(
        @SerializedName("name") val name: String
    )
}