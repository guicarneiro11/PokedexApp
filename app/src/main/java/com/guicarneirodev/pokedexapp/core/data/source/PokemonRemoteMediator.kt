package com.guicarneirodev.pokedexapp.core.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.guicarneirodev.pokedexapp.core.database.PokemonDatabase
import com.guicarneirodev.pokedexapp.core.database.entity.PokemonEntity
import com.guicarneirodev.pokedexapp.core.database.entity.PokemonRemoteKeys
import com.guicarneirodev.pokedexapp.core.network.ApiService

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediator(
    private val api: ApiService,
    private val database: PokemonDatabase
) : RemoteMediator<Int, PokemonEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> {
                    val firstItem = state.firstItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    val remoteKeys = database.pokemonRemoteKeysDao().getRemoteKeysById(firstItem.id)
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    remoteKeys.prevKey
                }
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    val remoteKeys = database.pokemonRemoteKeysDao().getRemoteKeysById(lastItem.id)
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    remoteKeys.nextKey
                }
            }

            if (page != null && page * 20 >= 150) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val response = api.getPokemonList(
                limit = 150,
                offset = page?.let { it * 20 } ?: 0
            )

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.pokemonRemoteKeysDao().clearAll()
                    database.pokemonDao().clearAll()
                }

                val prevKey = if (page == null) null else page - 1
                val nextKey = if (response.results.isEmpty() || (page?.let { it * 20 } ?: 0) + response.results.size >= 150)
                    null
                else
                    (page ?: 0) + 1

                val keys = response.results.map { pokemon ->
                    PokemonRemoteKeys(
                        pokemonId = pokemon.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                database.pokemonRemoteKeysDao().insertAll(keys)
                database.pokemonDao().insertAll(response.results.map {
                    PokemonEntity(
                        id = it.id,
                        name = it.name,
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${it.id}.png"
                    )
                })
            }

            return MediatorResult.Success(endOfPaginationReached = response.results.isEmpty() || (page?.let { it * 20 } ?: 0) + response.results.size >= 150)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}