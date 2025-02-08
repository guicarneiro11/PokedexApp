package com.guicarneirodev.pokedexapp.core.domain.error

import retrofit2.HttpException
import java.io.IOException
import android.database.sqlite.SQLiteException

sealed class AppError : Exception() {
    // Network Errors
    data class Network(override val message: String? = null) : AppError()
    data class NoInternetConnection(override val message: String? = null) : AppError()
    data class Timeout(override val message: String? = null) : AppError()

    // API Errors
    data class NotFound(override val message: String? = null) : AppError()
    data class Server(override val message: String? = null) : AppError()
    data class Unauthorized(override val message: String? = null) : AppError()

    // Database Errors
    data class Database(override val message: String? = null) : AppError()
    data class DatabaseNotFound(override val message: String? = null) : AppError()
    data class DatabaseCorrupted(override val message: String? = null) : AppError()

    // Others
    data class Unknown(override val message: String? = null) : AppError()
}

fun Exception.toAppError(): AppError = when (this) {
    is HttpException -> when (code()) {
        401 -> AppError.Unauthorized("Unauthorized access")
        403 -> AppError.Unauthorized("Forbidden access")
        404 -> AppError.NotFound("Pokémon not found")
        in 500..599 -> AppError.Server("Server error")
        else -> AppError.Network("Network error")
    }
    is IOException -> when {
        message?.contains("timeout", ignoreCase = true) == true ->
            AppError.Timeout("Request timed out")
        message?.contains("Unable to resolve host", ignoreCase = true) == true ->
            AppError.NoInternetConnection("No internet connection")
        else -> AppError.Network("Network error")
    }
    is SQLiteException -> when {
        message?.contains("no such table", ignoreCase = true) == true ->
            AppError.DatabaseNotFound("Table not found")
        message?.contains("corrupt", ignoreCase = true) == true ->
            AppError.DatabaseCorrupted("Database is corrupted")
        else -> AppError.Database("Database error")
    }
    else -> AppError.Unknown(message)
}