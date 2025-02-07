package com.guicarneirodev.pokedexapp.core.network

import com.guicarneirodev.pokedexapp.core.network.models.PokemonDetailsResponse
import com.guicarneirodev.pokedexapp.core.network.models.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = MAX_POKEMON,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    @Headers("Cache-Control: max-age=3600")
    @GET("pokemon/{nameOrId}")
    suspend fun getPokemonDetails(
        @Path("nameOrId") nameOrId: String
    ): PokemonDetailsResponse

    companion object {
        const val ITEMS_PER_PAGE = 20
        const val MAX_POKEMON = 150
    }
}