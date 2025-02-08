package com.guicarneirodev.pokedexapp

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.guicarneirodev.pokedexapp.core.data.source.PokemonRemoteMediator
import com.guicarneirodev.pokedexapp.core.database.PokemonDatabase
import com.guicarneirodev.pokedexapp.core.database.dao.PokemonDao
import com.guicarneirodev.pokedexapp.core.database.dao.PokemonRemoteKeysDao
import com.guicarneirodev.pokedexapp.core.database.entity.PokemonEntity
import com.guicarneirodev.pokedexapp.core.database.entity.PokemonRemoteKeys
import com.guicarneirodev.pokedexapp.core.network.ApiService
import com.guicarneirodev.pokedexapp.core.network.models.PokemonListResponse
import com.guicarneirodev.pokedexapp.core.network.models.PokemonResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

@OptIn(ExperimentalPagingApi::class)
class PokemonRemoteMediatorTest {
    private val api = mockk<ApiService>()
    private val db = mockk<PokemonDatabase>()
    private val pokemonDao = mockk<PokemonDao>()
    private val remoteKeysDao = mockk<PokemonRemoteKeysDao>()
    private lateinit var remoteMediator: PokemonRemoteMediator

    @Before
    fun setup() {
        every { db.pokemonDao() } returns pokemonDao
        every { db.pokemonRemoteKeysDao() } returns remoteKeysDao
        every { db.withTransaction(any<suspend () -> Any>()) } coAnswers {
            firstArg<suspend () -> Any>().invoke()
        }

        remoteMediator = PokemonRemoteMediator(api, db)
    }

    @Test
    fun `initialize returns SKIP_INITIAL_REFRESH when database has data`() = runTest {
        coEvery { pokemonDao.getCount() } returns 150

        val result = remoteMediator.initialize()
        assertEquals(RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH, result)
    }

    @Test
    fun `initialize returns LAUNCH_INITIAL_REFRESH when database is empty`() = runTest {
        coEvery { pokemonDao.getCount() } returns 0

        val result = remoteMediator.initialize()
        assertEquals(RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH, result)
    }

    @Test
    fun `load refresh success when data is inserted into database`() = runTest {
        val pagingState = PagingState<Int, PokemonEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = ApiService.ITEMS_PER_PAGE),
            leadingPlaceholderCount = 0
        )

        val response = PokemonListResponse(
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

        coEvery { api.getPokemonList(any(), any()) } returns response
        coEvery { remoteKeysDao.clearAll() } just runs
        coEvery { pokemonDao.clearAll() } just runs
        coEvery { remoteKeysDao.insertAll(any()) } just runs
        coEvery { pokemonDao.insertAll(any()) } just runs

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)

        coVerify {
            remoteKeysDao.clearAll()
            pokemonDao.clearAll()
            remoteKeysDao.insertAll(any())
            pokemonDao.insertAll(any())
        }
    }

    @Test
    fun `load returns error result when API fails`() = runTest {
        val pagingState = PagingState<Int, PokemonEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = ApiService.ITEMS_PER_PAGE),
            leadingPlaceholderCount = 0
        )

        coEvery { api.getPokemonList(any(), any()) } throws
                UnknownHostException()

        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    @Test
    fun `load append returns success with endOfPagination when max pokemon reached`() = runTest {
        PagingState<Int, PokemonEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = ApiService.ITEMS_PER_PAGE),
            leadingPlaceholderCount = 0
        )

        // Simular última página
        val lastItem = PokemonEntity(id = 150, name = "last", imageUrl = "url")
        val lastPage = listOf(lastItem)
        val pagingSourcePage = PagingSource.LoadResult.Page(
            data = lastPage,
            prevKey = null,
            nextKey = null
        )

        coEvery {
            remoteKeysDao.getRemoteKeysById(any())
        } returns PokemonRemoteKeys(
            pokemonId = 150,
            prevKey = 6,
            nextKey = null
        )

        val mockState = mockk<PagingState<Int, PokemonEntity>> {
            every { pages } returns listOf(pagingSourcePage)
            every { config } returns PagingConfig(pageSize = ApiService.ITEMS_PER_PAGE)
        }

        val result = remoteMediator.load(LoadType.APPEND, mockState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `load prepend returns success with end of pagination when first page`() = runTest {
        PagingState<Int, PokemonEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = ApiService.ITEMS_PER_PAGE),
            leadingPlaceholderCount = 0
        )

        // Simular primeira página
        val firstItem = PokemonEntity(id = 1, name = "first", imageUrl = "url")
        val firstPage = listOf(firstItem)
        val pagingSourcePage = PagingSource.LoadResult.Page(
            data = firstPage,
            prevKey = null,
            nextKey = null
        )

        coEvery {
            remoteKeysDao.getRemoteKeysById(any())
        } returns PokemonRemoteKeys(
            pokemonId = 1,
            prevKey = null,
            nextKey = 1
        )

        val mockState = mockk<PagingState<Int, PokemonEntity>> {
            every { pages } returns listOf(pagingSourcePage)
            every { config } returns PagingConfig(pageSize = ApiService.ITEMS_PER_PAGE)
        }

        val result = remoteMediator.load(LoadType.PREPEND, mockState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}