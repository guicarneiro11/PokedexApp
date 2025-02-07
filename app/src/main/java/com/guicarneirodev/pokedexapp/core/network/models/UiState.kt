package com.guicarneirodev.pokedexapp.core.network.models

import com.guicarneirodev.pokedexapp.core.domain.error.AppError

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val error: AppError) : UiState<Nothing>()
}