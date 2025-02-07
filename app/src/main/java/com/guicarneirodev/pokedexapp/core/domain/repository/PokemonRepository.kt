package com.guicarneirodev.pokedexapp.core.domain.repository

import androidx.paging.PagingData
import com.guicarneirodev.pokedexapp.core.domain.model.Pokemon
import com.guicarneirodev.pokedexapp.core.domain.model.PokemonDetails
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonList(): Flow<PagingData<Pokemon>>
    fun searchPokemon(query: String): Flow<PagingData<Pokemon>>
    suspend fun getPokemonDetails(id: Int): PokemonDetails
}