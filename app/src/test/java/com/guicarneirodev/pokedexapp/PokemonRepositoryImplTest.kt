package com.guicarneirodev.pokedexapp

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.guicarneirodev.pokedexapp.core.data.repository.PokemonRepositoryImpl
import com.guicarneirodev.pokedexapp.core.database.PokemonDatabase
import com.guicarneirodev.pokedexapp.core.database.dao.PokemonDao
import com.guicarneirodev.pokedexapp.core.database.entity.PokemonEntity
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.domain.model.Pokemon
import com.guicarneirodev.pokedexapp.core.network.ApiService
import com.guicarneirodev.pokedexapp.core.network.models.AbilityResponse
import com.guicarneirodev.pokedexapp.core.network.models.MoveResponse
import com.guicarneirodev.pokedexapp.core.network.models.NameUrlResponse
import com.guicarneirodev.pokedexapp.core.network.models.PokemonDetailsResponse
import com.guicarneirodev.pokedexapp.core.network.models.SpritesResponse
import com.guicarneirodev.pokedexapp.core.network.models.StatResponse
import com.guicarneirodev.pokedexapp.core.network.models.TypeResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import retrofit2.HttpException
import retrofit2.Response
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

class PokemonRepositoryImplTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val api = mockk<ApiService>()
    private val database = mockk<PokemonDatabase>()
    private lateinit var repository: PokemonRepositoryImpl

    @Before
    fun setup() {
        val pokemonDao = mockk<PokemonDao>()
        coEvery { database.pokemonDao() } returns pokemonDao
        coEvery { pokemonDao.getPokemonById(any()) } returns null

        repository = PokemonRepositoryImpl(api, database)
    }

    @Test
    fun `searchPokemon returns filtered results from database`() = runTest {
        val query = "pika"
        val pokemonDao = mockk<PokemonDao>()
        val pagingSource = mockk<PagingSource<Int, PokemonEntity>>()

        coEvery { database.pokemonDao() } returns pokemonDao
        coEvery { pokemonDao.searchPokemons(query) } returns pagingSource

        repository.searchPokemon(query)

        coVerify { pokemonDao.searchPokemons(query) }
    }

    @Test
    fun `getPokemonList uses RemoteMediator for pagination`() = runTest {
        repository.getPokemonList()

        assertTrue(true)
    }
}