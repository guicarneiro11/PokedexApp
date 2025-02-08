package com.guicarneirodev.pokedexapp

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.guicarneirodev.pokedexapp.core.data.source.PokemonPagingSource
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.sql.SQLException
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.domain.model.Pokemon
import com.guicarneirodev.pokedexapp.core.network.ApiService
import com.guicarneirodev.pokedexapp.core.network.models.PokemonListResponse
import com.guicarneirodev.pokedexapp.core.network.models.PokemonResult
import io.mockk.coVerify

@OptIn(ExperimentalPagingApi::class)
class PokemonPagingSourceTest {
    private val api = mockk<ApiService>()
    private lateinit var pagingSource: PokemonPagingSource

    @Before
    fun setup() {
        pagingSource = PokemonPagingSource(api)
    }

    @Test
    fun `getRefreshKey returns correct key`() = runTest {
        // Criar estado mock com dados
        val state = PagingState<Int, Pokemon>(
            pages = listOf(
                PagingSource.LoadResult.Page(
                    data = listOf(Pokemon(1, "bulbasaur", "url")),
                    prevKey = null,
                    nextKey = 1
                )
            ),
            anchorPosition = 0,
            config = PagingConfig(pageSize = ApiService.ITEMS_PER_PAGE),
            leadingPlaceholderCount = 0
        )

        val refreshKey = pagingSource.getRefreshKey(state)
        assertEquals(0, refreshKey)
    }

    @Test
    fun `load returns success page with pokemon list`() = runTest {
        // Mock resposta da API
        val mockResponse = PokemonListResponse(
            count = ApiService.ITEMS_PER_PAGE,
            next = "next_url",
            previous = null,
            results = List(ApiService.ITEMS_PER_PAGE) { index ->
                PokemonResult(
                    name = "pokemon$index",
                    url = "https://pokeapi.co/api/v2/pokemon/${index + 1}/"
                )
            }
        )

        coEvery {
            api.getPokemonList(
                offset = 0,
                limit = ApiService.ITEMS_PER_PAGE
            )
        } returns mockResponse

        // Carregar primeira página
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = ApiService.ITEMS_PER_PAGE,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(ApiService.ITEMS_PER_PAGE, page.data.size)
        assertNull(page.prevKey)
        assertEquals(1, page.nextKey)
    }

    @Test
    fun `load with query returns filtered results`() = runTest {
        val query = "saur"
        val filteredPagingSource = PokemonPagingSource(api, query)

        val mockResponse = PokemonListResponse(
            count = 3,
            next = null,
            previous = null,
            results = listOf(
                PokemonResult("bulbasaur", "https://pokeapi.co/api/v2/pokemon/1/"),
                PokemonResult("ivysaur", "https://pokeapi.co/api/v2/pokemon/2/"),
                PokemonResult("venusaur", "https://pokeapi.co/api/v2/pokemon/3/")
            )
        )

        coEvery {
            api.getPokemonList(any(), any())
        } returns mockResponse

        val result = filteredPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = ApiService.ITEMS_PER_PAGE,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(3, page.data.size)
        assertTrue(page.data.all { it.name.contains(query) })
    }

    @Test
    fun `load returns error when API throws exception`() = runTest {
        coEvery {
            api.getPokemonList(any(), any())
        } throws UnknownHostException()

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = ApiService.ITEMS_PER_PAGE,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        val error = (result as PagingSource.LoadResult.Error).throwable
        assertTrue(error is AppError.NoInternetConnection)
    }

    @Test
    fun `load returns error when no results found for query`() = runTest {
        val query = "nonexistent"
        val filteredPagingSource = PokemonPagingSource(api, query)

        val mockResponse = PokemonListResponse(
            count = 0,
            next = null,
            previous = null,
            results = emptyList()
        )

        coEvery {
            api.getPokemonList(any(), any())
        } returns mockResponse

        val result = filteredPagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = ApiService.ITEMS_PER_PAGE,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        val error = (result as PagingSource.LoadResult.Error).throwable
        assertTrue(error is AppError.NotFound)
        assertEquals("No Pokémon found for '$query'", error.message)
    }

    @Test
    fun `load returns empty page when offset exceeds max pokemon`() = runTest {
        // Usar um offset que realmente excede o máximo
        val page = (ApiService.MAX_POKEMON / ApiService.ITEMS_PER_PAGE) + 1
        val offset = page * ApiService.ITEMS_PER_PAGE

        // Neste caso, nem precisamos mockar a API porque o PagingSource
        // deve retornar uma página vazia antes mesmo de chamar a API
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = page,
                loadSize = ApiService.ITEMS_PER_PAGE,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)
        val resultPage = result as PagingSource.LoadResult.Page
        assertTrue(resultPage.data.isEmpty())
        assertNull(resultPage.prevKey)
        assertNull(resultPage.nextKey)

        // Verificar que a API nem foi chamada
        coVerify(exactly = 0) { api.getPokemonList(any(), any()) }
    }
}