package com.guicarneirodev.pokedexapp.core.data.repository

import android.database.sqlite.SQLiteException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.guicarneirodev.pokedexapp.core.data.source.PokemonRemoteMediator
import com.guicarneirodev.pokedexapp.core.database.PokemonDatabase
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.domain.error.toAppError
import com.guicarneirodev.pokedexapp.core.domain.model.Pokemon
import com.guicarneirodev.pokedexapp.core.domain.model.PokemonDetails
import com.guicarneirodev.pokedexapp.core.domain.repository.PokemonRepository
import com.guicarneirodev.pokedexapp.core.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@OptIn(ExperimentalPagingApi::class)
class PokemonRepositoryImpl(
    private val api: ApiService,
    private val database: PokemonDatabase
) : PokemonRepository {

    override fun getPokemonList(): Flow<PagingData<Pokemon>> {
        return Pager(
            config = PagingConfig(
                pageSize = ApiService.ITEMS_PER_PAGE,
                enablePlaceholders = true,
                initialLoadSize = ApiService.ITEMS_PER_PAGE * 2,
                prefetchDistance = ApiService.ITEMS_PER_PAGE,
                maxSize = ApiService.MAX_POKEMON
            ),
            remoteMediator = PokemonRemoteMediator(api, database),
            pagingSourceFactory = { database.pokemonDao().getPokemons() }
        ).flow.map { pagingData ->
            pagingData.map { entity ->
                Pokemon(
                    id = entity.id,
                    name = entity.name,
                    imageUrl = entity.imageUrl
                )
            }
        }
    }

    override fun searchPokemon(query: String): Flow<PagingData<Pokemon>> {
        return if (query.isEmpty()) {
            getPokemonList()
        } else {
            Pager(
                config = PagingConfig(
                    pageSize = ApiService.ITEMS_PER_PAGE,
                    enablePlaceholders = false,
                    maxSize = ApiService.MAX_POKEMON
                )
            ) {
                database.pokemonDao().searchPokemons(query.lowercase())
            }.flow.map { pagingData ->
                pagingData.map { entity ->
                    Pokemon(
                        id = entity.id,
                        name = entity.name,
                        imageUrl = entity.imageUrl
                    )
                }
            }
        }
    }

    override suspend fun getPokemonDetails(id: Int): PokemonDetails {
        try {
            database.pokemonDao().getPokemonById(id)?.let { entity ->
                if (!PokemonDatabase.isDataStale(entity.lastUpdate)) {
                    return api.getPokemonDetails(id.toString()).toPokemonDetails()
                }
            }

            return withContext(Dispatchers.IO) {
                val details = api.getPokemonDetails(id.toString())
                details.toPokemonDetails()
            }
        } catch (e: Exception) {
            throw when (e) {
                is UnknownHostException -> AppError.NoInternetConnection("Check your internet connection")
                is SocketTimeoutException -> AppError.Timeout("Request timed out")
                is HttpException -> when (e.code()) {
                    404 -> AppError.NotFound("Pokémon #$id not found")
                    in 500..599 -> AppError.Server("Server error, try again later")
                    else -> e.toAppError()
                }
                is SQLiteException -> AppError.Database("Database error")
                else -> e.toAppError()
            }
        }
    }
}