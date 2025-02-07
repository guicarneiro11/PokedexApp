package com.guicarneirodev.pokedexapp.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guicarneirodev.pokedexapp.core.database.entity.PokemonRemoteKeys

@Dao
interface PokemonRemoteKeysDao {
    @Query("SELECT * FROM pokemon_remote_keys WHERE pokemonId = :id")
    suspend fun getRemoteKeysById(id: Int): PokemonRemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<PokemonRemoteKeys>)

    @Query("DELETE FROM pokemon_remote_keys")
    suspend fun clearAll()
}