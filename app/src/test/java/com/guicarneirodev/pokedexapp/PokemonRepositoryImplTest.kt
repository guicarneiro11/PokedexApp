package com.guicarneirodev.pokedexapp

import com.guicarneirodev.pokedexapp.core.data.repository.PokemonRepositoryImpl
import com.guicarneirodev.pokedexapp.core.database.PokemonDatabase
import com.guicarneirodev.pokedexapp.core.database.dao.PokemonDao
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.network.ApiService
import com.guicarneirodev.pokedexapp.core.network.models.AbilityResponse
import com.guicarneirodev.pokedexapp.core.network.models.MoveResponse
import com.guicarneirodev.pokedexapp.core.network.models.NameUrlResponse
import com.guicarneirodev.pokedexapp.core.network.models.PokemonDetailsResponse
import com.guicarneirodev.pokedexapp.core.network.models.SpritesResponse
import com.guicarneirodev.pokedexapp.core.network.models.StatResponse
import com.guicarneirodev.pokedexapp.core.network.models.TypeResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun `getPokemonDetails should include abilities and moves`() = runTest {
        val pokemonId = 1
        val response = PokemonDetailsResponse(
            id = pokemonId,
            name = "bulbasaur",
            height = 7,
            weight = 69,
            types = listOf(TypeResponse(TypeResponse.Type("grass"))),
            stats = listOf(StatResponse(45, StatResponse.Stat("hp"))),
            sprites = SpritesResponse("url"),
            abilities = listOf(
                AbilityResponse(NameUrlResponse("overgrow", ""), false),
                AbilityResponse(NameUrlResponse("chlorophyll", ""), true)
            ),
            moves = listOf(
                MoveResponse(
                    move = NameUrlResponse("tackle", "https://pokeapi.co/api/v2/move/33/"),
                    type = TypeResponse(TypeResponse.Type("normal"))
                )
            )
        )
        coEvery { api.getPokemonDetails(pokemonId.toString()) } returns response

        val result = repository.getPokemonDetails(pokemonId)

        assertEquals(2, result.abilities.size)
        assertEquals("overgrow", result.abilities[0].name)
        assertFalse(result.abilities[0].isHidden)
        assertEquals(1, result.moves.size)
        assertEquals("tackle", result.moves[0].name)
    }

    @Test(expected = AppError.NotFound::class)
    fun `getPokemonDetails should throw NotFound for 404 error`() = runTest {
        val pokemonId = 1
        coEvery { api.getPokemonDetails(pokemonId.toString()) } throws HttpException(
            Response.error<Any>(404, "".toResponseBody())
        )

        repository.getPokemonDetails(pokemonId)
    }

    @Test(expected = AppError.NoInternetConnection::class)
    fun `getPokemonDetails should throw NoInternetConnection on network error`() = runTest {
        val pokemonId = 1
        coEvery { api.getPokemonDetails(pokemonId.toString()) } throws UnknownHostException()

        repository.getPokemonDetails(pokemonId)
    }
}