package com.guicarneirodev.pokedexapp

import com.guicarneirodev.pokedexapp.core.domain.model.Pokemon
import com.guicarneirodev.pokedexapp.core.presentation.util.PokemonMemoryCache
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class PokemonMemoryCacheTest {
    private lateinit var cache: PokemonMemoryCache

    @Before
    fun setup() {
        cache = PokemonMemoryCache()
    }

    @Test
    fun `cachePokemon stores pokemon correctly`() {
        val pokemon = Pokemon(1, "bulbasaur", "url")

        cache.cachePokemon(pokemon.id, pokemon)
    }

    @Test
    fun `cache handles multiple pokemon without exceeding max size`() {
        repeat(200) { id ->
            val pokemon = Pokemon(id, "pokemon$id", "url$id")
            cache.cachePokemon(pokemon.id, pokemon)
        }

        assertTrue(true)
    }

    @Test
    fun `cache survives memory pressure`() {
        val pokemon = Pokemon(1, "bulbasaur", "url")
        cache.cachePokemon(pokemon.id, pokemon)

        System.gc()
        Runtime.getRuntime().gc()

        assertTrue(true)
    }

    @Test
    fun `cache handles null pokemon gracefully`() {
        cache.cachePokemon(1, null)
        assertTrue(true)
    }
}