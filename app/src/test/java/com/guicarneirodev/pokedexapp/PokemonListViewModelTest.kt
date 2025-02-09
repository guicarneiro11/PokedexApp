package com.guicarneirodev.pokedexapp

import androidx.paging.PagingData
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.domain.model.Pokemon
import com.guicarneirodev.pokedexapp.core.domain.repository.PokemonRepository
import com.guicarneirodev.pokedexapp.core.presentation.util.PokemonMemoryCache
import com.guicarneirodev.pokedexapp.features.list.presentation.PokemonListViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {
    private val repository = mockk<PokemonRepository>()
    private val cache = mockk<PokemonMemoryCache>()
    private lateinit var viewModel: PokemonListViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { cache.cachePokemon(any(), any()) } just Runs
        viewModel = PokemonListViewModel(repository, cache)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `search query changes trigger repository search with debounce`() = runTest {
        val mockPokemon = Pokemon(1, "pikachu", "url")
        val mockPagingData = PagingData.from(listOf(mockPokemon))
        val mockFlow = flow { emit(mockPagingData) }

        coEvery { repository.getPokemonList() } returns mockFlow
        coEvery { repository.searchPokemon(any()) } returns mockFlow

        val job = launch {
            viewModel.pokemonList.collect { _ ->

            }
        }

        viewModel.onSearchQueryChange("pika")
        advanceTimeBy(400)

        coVerify { repository.searchPokemon("pika") }
        job.cancel()
    }

    @Test
    fun `prefetchInitialData handles errors correctly`() = runTest {
        val exception = UnknownHostException("No internet")
        coEvery { repository.getPokemonList() } returns flow {
            throw exception
        }

        val errorStates = mutableListOf<AppError?>()

        val collectorJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.prefetchError.collect { error ->
                errorStates.add(error)
            }
        }

        viewModel.prefetchInitialData()

        advanceUntilIdle()

        try {
            assertTrue("Should have collected at least one error", errorStates.isNotEmpty())

            val error = errorStates.firstOrNull { it != null }
            assertNotNull(
                "Expected an error state, but got null. Collected states: $errorStates",
                error
            )

            assertTrue(
                "Expected Unknown error with 'Unexpected error during prefetch' message, but got $error",
                error is AppError.Unknown && error.message == "Unexpected error during prefetch"
            )
        } finally {
            collectorJob.cancel()
        }
    }
}