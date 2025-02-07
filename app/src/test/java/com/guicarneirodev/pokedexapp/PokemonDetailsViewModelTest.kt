package com.guicarneirodev.pokedexapp

import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.domain.model.PokemonDetails
import com.guicarneirodev.pokedexapp.core.domain.model.Stat
import com.guicarneirodev.pokedexapp.core.domain.repository.PokemonRepository
import com.guicarneirodev.pokedexapp.core.network.models.UiState
import com.guicarneirodev.pokedexapp.features.details.presentation.PokemonDetailsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonDetailsViewModelTest {
    private val repository = mockk<PokemonRepository>()
    private lateinit var viewModel: PokemonDetailsViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        viewModel = PokemonDetailsViewModel(repository)
    }

    @Test
    fun `loadPokemonDetails sets success state`() = runTest {
        val mockDetails = PokemonDetails(
            id = 1,
            name = "bulbasaur",
            height = 0.7,
            weight = 6.9,
            types = listOf("grass"),
            stats = listOf(Stat("hp", 45)),
            imageUrl = "url",
            abilities = emptyList(),
            moves = emptyList()
        )
        coEvery { repository.getPokemonDetails(1) } returns mockDetails

        viewModel.loadPokemonDetails(1)

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        assertEquals(mockDetails, (state as UiState.Success).data)
    }

    @Test
    fun `loadPokemonDetails sets error state`() = runTest {
        val mockError = AppError.NotFound("Pokemon not found")
        coEvery { repository.getPokemonDetails(999) } throws mockError

        viewModel.loadPokemonDetails(999)

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Error)
        assertEquals(mockError, (state as UiState.Error).error)
    }
}