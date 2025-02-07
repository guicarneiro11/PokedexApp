package com.guicarneirodev.pokedexapp

import androidx.paging.PagingSource
import com.guicarneirodev.pokedexapp.core.data.source.PokemonPagingSource
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.network.ApiService
import com.guicarneirodev.pokedexapp.core.network.models.PokemonListResponse
import com.guicarneirodev.pokedexapp.core.network.models.PokemonResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonPagingSourceTest {
    private val api = mockk<ApiService>()
    private lateinit var pagingSource: PokemonPagingSource

    @Before
    fun setup() {
        pagingSource = PokemonPagingSource(api)
    }

    @Test
    fun `load returns page with all pokemon when no query`() = runTest {
        val mockResponse = PokemonListResponse(
            count = 150,
            next = null,
            previous = null,
            results = (1..150).map {
                PokemonResult(
                    name = "pokemon$it",
                    url = "https://pokeapi.co/api/v2/pokemon/$it/"
                )
            }
        )
        coEvery { api.getPokemonList(limit = 150) } returns mockResponse

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 150,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(150, page.data.size)
    }

    @Test
    fun `load returns filtered pokemon when query provided`() = runTest {
        val mockResponse = PokemonListResponse(
            count = 150,
            next = null,
            previous = null,
            results = listOf(
                PokemonResult("pikachu", "https://pokeapi.co/api/v2/pokemon/25/"),
                PokemonResult("raichu", "https://pokeapi.co/api/v2/pokemon/26/")
            )
        )
        coEvery { api.getPokemonList(limit = 150) } returns mockResponse

        val queryPagingSource = PokemonPagingSource(api, "pika")
        val result = queryPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 150,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(1, page.data.size)
        assertEquals("pikachu", page.data[0].name)
    }

    @Test
    fun `load returns error when no pokemon found for query`() = runTest {
        val mockResponse = PokemonListResponse(
            count = 0,
            next = null,
            previous = null,
            results = emptyList()
        )
        coEvery { api.getPokemonList(limit = 150) } returns mockResponse

        val queryPagingSource = PokemonPagingSource(api, "nonexistent")
        val result = queryPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 150,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        val error = (result as PagingSource.LoadResult.Error).throwable
        assertTrue(error is AppError.NotFound)
    }
}