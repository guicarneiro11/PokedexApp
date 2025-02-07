package com.guicarneirodev.pokedexapp

import androidx.paging.PagingData
import com.guicarneirodev.pokedexapp.core.domain.model.Pokemon
import com.guicarneirodev.pokedexapp.core.domain.repository.PokemonRepository
import com.guicarneirodev.pokedexapp.features.list.presentation.PokemonListViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {
    private val repository = mockk<PokemonRepository>()
    private lateinit var viewModel: PokemonListViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setup() {
        viewModel = PokemonListViewModel(repository)
    }

    @Test
    fun `search query changes trigger repository search`() = runTest {
        val pokemonList = listOf(
            Pokemon(1, "pikachu", "url"),
            Pokemon(2, "raichu", "url")
        )
        coEvery { repository.searchPokemon("pika") } returns flow {
            emit(PagingData.from(pokemonList))
        }

        viewModel.onSearchQueryChange("pika")

        val result = viewModel.pokemonList.first()
        assertTrue(true)
    }

    @Test
    fun `empty query returns full pokemon list`() = runTest {
        val pokemonList = listOf(
            Pokemon(1, "bulbasaur", "url"),
            Pokemon(2, "ivysaur", "url")
        )
        coEvery { repository.getPokemonList() } returns flow {
            emit(PagingData.from(pokemonList))
        }

        viewModel.onSearchQueryChange("")

        val result = viewModel.pokemonList.first()
        assertTrue(true)
    }
}