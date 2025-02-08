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

    private val pokemonDao = database.pokemonDao()
    private val remoteKeysDao = database.pokemonRemoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        return if (database.pokemonDao().getCount() > 0) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PokemonEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    nextKey
                }
            }

            val offset = page?.let { it * ApiService.ITEMS_PER_PAGE } ?: 0
            val limit = minOf(state.config.pageSize, ApiService.MAX_POKEMON - offset)

            if (offset >= ApiService.MAX_POKEMON) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val response = api.getPokemonList(
                offset = offset,
                limit = limit
            )

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearAll()
                    pokemonDao.clearAll()
                }

                val prevKey = if (offset == 0) null else page?.minus(1)
                val nextKey = if (response.results.isEmpty() || offset + limit >= ApiService.MAX_POKEMON) {
                    null
                } else {
                    page?.plus(1) ?: 1
                }

                val keys = response.results.map { pokemon ->
                    PokemonRemoteKeys(
                        pokemonId = pokemon.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                remoteKeysDao.insertAll(keys)
                pokemonDao.insertAll(response.results.map {
                    PokemonEntity(
                        id = it.id,
                        name = it.name,
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${it.id}.png"
                    )
                })
            }

            MediatorResult.Success(endOfPaginationReached = offset + limit >= ApiService.MAX_POKEMON)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PokemonEntity>): PokemonRemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { pokemon ->
            remoteKeysDao.getRemoteKeysById(pokemon.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PokemonEntity>): PokemonRemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { pokemon ->
            remoteKeysDao.getRemoteKeysById(pokemon.id)
        }
    }
}