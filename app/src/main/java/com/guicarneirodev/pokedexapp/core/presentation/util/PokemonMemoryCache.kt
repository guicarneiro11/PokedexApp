package com.guicarneirodev.pokedexapp.core.presentation.util

import android.util.LruCache
import com.guicarneirodev.pokedexapp.core.domain.model.Pokemon
import java.lang.ref.WeakReference

class PokemonMemoryCache {
    private val pokemonCache = LruCache<Int, WeakReference<Pokemon>>(150)

    fun cachePokemon(id: Int, pokemon: Pokemon?) {
        pokemon?.let {
            pokemonCache.put(id, WeakReference(it))
        }
    }
}