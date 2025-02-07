package com.guicarneirodev.pokedexapp.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guicarneirodev.pokedexapp.core.database.entity.PokemonEntity

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon ORDER BY id")
    fun getPokemons(): PagingSource<Int, PokemonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemons: List<PokemonEntity>)

    @Query("DELETE FROM pokemon")
    suspend fun clearAll()

    @Query("SELECT * FROM pokemon WHERE id = :id")
    suspend fun getPokemonById(id: Int): PokemonEntity?

    @Query("SELECT last_update FROM pokemon ORDER BY last_update DESC LIMIT 1")
    suspend fun getLastUpdateTime(): Long?

    @Dao
    interface PokemonDao {
        @Query("SELECT * FROM pokemon WHERE name LIKE :query ORDER BY id")
        fun searchPokemons(query: String): PagingSource<Int, PokemonEntity>
    }
}