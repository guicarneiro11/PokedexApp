package com.guicarneirodev.pokedexapp

import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.domain.model.Ability
import com.guicarneirodev.pokedexapp.core.domain.model.Move
import com.guicarneirodev.pokedexapp.core.domain.model.PokemonDetails
import com.guicarneirodev.pokedexapp.core.domain.model.Stat
import com.guicarneirodev.pokedexapp.core.domain.repository.PokemonRepository
import com.guicarneirodev.pokedexapp.core.network.models.UiState
import com.guicarneirodev.pokedexapp.features.details.presentation.PokemonDetailsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonDetailsViewModelTest {
    private val repository = mockk<PokemonRepository>()
    private lateinit var viewModel: PokemonDetailsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = PokemonDetailsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadPokemonDetails sets loading state initially`() = runTest {
        val states = mutableListOf<UiState<PokemonDetails>>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { states.add(it) }
        }

        coEvery { repository.getPokemonDetails(any()) } coAnswers {
            delay(1000)
            mockPokemonDetails
        }

        viewModel.loadPokemonDetails(1)

        assertTrue(states[0] is UiState.Loading)

        job.cancel()
    }

    @Test
    fun `loadPokemonDetails sets success state with pokemon details`() = runTest {
        coEvery { repository.getPokemonDetails(1) } returns mockPokemonDetails

        viewModel.loadPokemonDetails(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(mockPokemonDetails, (state as UiState.Success).data)
    }

    @Test
    fun `loadPokemonDetails sets error state when repository throws error`() = runTest {
        val error = AppError.NotFound("Pokemon not found")
        coEvery { repository.getPokemonDetails(999) } throws error

        viewModel.loadPokemonDetails(999)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals(error, (state as UiState.Error).error)
    }

    @Test
    fun `loadPokemonDetails handles network errors correctly`() = runTest {
        coEvery { repository.getPokemonDetails(any()) } throws UnknownHostException()

        viewModel.loadPokemonDetails(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertTrue((state as UiState.Error).error is AppError.Unknown)
    }

    private companion object {
        val mockPokemonDetails = PokemonDetails(
            id = 1,
            name = "Bulbasaur",
            height = 0.7,
            weight = 6.9,
            types = listOf("grass", "poison"),
            stats = listOf(
                Stat("hp", 45),
                Stat("attack", 49)
            ),
            imageUrl = "https://example.com/bulbasaur.png",
            abilities = listOf(
                Ability("overgrow", false),
                Ability("chlorophyll", true)
            ),
            moves = listOf(
                Move("tackle", "normal"),
                Move("vine-whip", "grass")
            )
        )
    }
}