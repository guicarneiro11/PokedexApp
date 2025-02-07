package com.guicarneirodev.pokedexapp.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    @ColumnInfo(name = "last_update") val lastUpdate: Long = System.currentTimeMillis()
)