package com.guicarneirodev.pokedexapp.core.network.models

import com.google.gson.annotations.SerializedName

data class AbilityResponse(
    val ability: NameUrlResponse,
    @SerializedName("is_hidden") val isHidden: Boolean
)
