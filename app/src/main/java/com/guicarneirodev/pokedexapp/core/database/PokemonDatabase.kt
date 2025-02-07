package com.guicarneirodev.pokedexapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.guicarneirodev.pokedexapp.core.database.dao.PokemonDao
import com.guicarneirodev.pokedexapp.core.database.dao.PokemonRemoteKeysDao
import com.guicarneirodev.pokedexapp.core.database.entity.PokemonEntity
import com.guicarneirodev.pokedexapp.core.database.entity.PokemonRemoteKeys

@Database(
    entities = [PokemonEntity::class, PokemonRemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonRemoteKeysDao(): PokemonRemoteKeysDao

    companion object {
        private const val CACHE_TIMEOUT = 30 * 60 * 1000

        fun isDataStale(lastUpdate: Long): Boolean {
            return System.currentTimeMillis() - lastUpdate > CACHE_TIMEOUT
        }
    }
}