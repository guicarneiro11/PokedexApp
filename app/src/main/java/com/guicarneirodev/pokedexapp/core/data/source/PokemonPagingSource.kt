package com.guicarneirodev.pokedexapp.core.data.source

import android.database.sqlite.SQLiteException
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.domain.error.toAppError
import com.guicarneirodev.pokedexapp.core.domain.model.Pokemon
import com.guicarneirodev.pokedexapp.core.network.ApiService
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class PokemonPagingSource(
    private val api: ApiService,
    private val query: String? = null
) : PagingSource<Int, Pokemon>() {
    override fun getRefreshKey(state: PagingState<Int, Pokemon>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(position)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Pokemon> {
        return try {
            val response = api.getPokemonList(limit = 150)

            val filteredResults = if (!query.isNullOrEmpty()) {
                response.results.filter {
                    it.name.lowercase().contains(query.lowercase())
                }
            } else {
                response.results
            }

            if (filteredResults.isEmpty() && !query.isNullOrEmpty()) {
                return LoadResult.Error(AppError.NotFound("No Pokémon found for '$query'"))
            }

            val pokemonList = filteredResults
                .take(150)
                .map { pokemonResult ->
                    Pokemon(
                        id = pokemonResult.id,
                        name = pokemonResult.name,
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemonResult.id}.png"
                    )
                }

            LoadResult.Page(
                data = pokemonList,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(
                when (e) {
                    is UnknownHostException -> AppError.NoInternetConnection("Check your internet connection")
                    is SocketTimeoutException -> AppError.Timeout("Request timed out")
                    is HttpException -> when (e.code()) {
                        in 500..599 -> AppError.Server("Server error, try again later")
                        else -> e.toAppError()
                    }
                    is SQLiteException -> AppError.Database("Database error")
                    else -> e.toAppError()
                }
            )
        }
    }
}