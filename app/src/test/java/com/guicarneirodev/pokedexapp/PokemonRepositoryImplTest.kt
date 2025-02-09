package com.guicarneirodev.pokedexapp

import androidx.paging.ExperimentalPagingApi
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
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class PokemonRepositoryImplTest {
    private val api = mockk<ApiService>()
    private val database = mockk<PokemonDatabase>()
    private val pokemonDao = mockk<PokemonDao>()
    private lateinit var repository: PokemonRepositoryImpl

    @Before
    fun setup() {
        every { database.pokemonDao() } returns pokemonDao
        repository = PokemonRepositoryImpl(api, database)
    }

    @Test
    fun `getPokemonDetails should return pokemon details successfully`() = runTest {
        val pokemonId = 1
        val mockResponse = PokemonDetailsResponse(
            id = pokemonId,
            name = "bulbasaur",
            height = 7,
            weight = 69,
            types = listOf(
                TypeResponse(TypeResponse.Type("grass")),
                TypeResponse(TypeResponse.Type("poison"))
            ),
            stats = listOf(
                StatResponse(45, StatResponse.Stat("hp")),
                StatResponse(49, StatResponse.Stat("attack")),
                StatResponse(49, StatResponse.Stat("defense"))
            ),
            sprites = SpritesResponse(frontDefault = "url"),
            abilities = listOf(
                AbilityResponse(
                    ability = NameUrlResponse("overgrow", "url"),
                    isHidden = false
                ),
                AbilityResponse(
                    ability = NameUrlResponse("chlorophyll", "url"),
                    isHidden = true
                )
            ),
            moves = listOf(
                MoveResponse(
                    move = NameUrlResponse("tackle", "https://pokeapi.co/api/v2/move/33/"),
                    type = TypeResponse(TypeResponse.Type("normal"))
                ),
                MoveResponse(
                    move = NameUrlResponse("vine-whip", "https://pokeapi.co/api/v2/move/22/"),
                    type = TypeResponse(TypeResponse.Type("grass"))
                )
            )
        )

        coEvery { pokemonDao.getPokemonById(pokemonId) } returns null
        coEvery { api.getPokemonDetails(pokemonId.toString()) } returns mockResponse

        val result = repository.getPokemonDetails(pokemonId)

        assertEquals(pokemonId, result.id)
        assertEquals("bulbasaur", result.name)
        assertEquals(0.7, result.height, 0.01)
        assertEquals(6.9, result.weight, 0.01)
        assertTrue(result.types.contains("grass"))
        assertEquals(2, result.abilities.size)
        assertEquals(2, result.moves.size)
        assertEquals(3, result.stats.size)
    }

    @Test(expected = AppError.NotFound::class)
    fun `getPokemonDetails should throw NotFound when pokemon doesnt exist`() = runTest {
        coEvery { pokemonDao.getPokemonById(any()) } returns null
        coEvery {
            api.getPokemonDetails(any())
        } throws HttpException(Response.error<Any>(404, "".toResponseBody()))

        repository.getPokemonDetails(999)
    }
}