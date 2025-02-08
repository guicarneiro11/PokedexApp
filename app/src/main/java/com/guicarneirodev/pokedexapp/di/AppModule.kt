package com.guicarneirodev.pokedexapp.di

import androidx.room.Room
import com.guicarneirodev.pokedexapp.core.data.repository.PokemonRepositoryImpl
import com.guicarneirodev.pokedexapp.core.database.PokemonDatabase
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.domain.repository.PokemonRepository
import com.guicarneirodev.pokedexapp.core.network.ApiService
import com.guicarneirodev.pokedexapp.core.presentation.util.PokemonMemoryCache
import com.guicarneirodev.pokedexapp.features.details.presentation.PokemonDetailsViewModel
import com.guicarneirodev.pokedexapp.features.list.presentation.PokemonListViewModel
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

val appModule = module {
    single { PokemonMemoryCache() }
    single {
        Room.databaseBuilder(get(), PokemonDatabase::class.java, "pokemon.db").build()
    }
    single {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                try {
                    chain.proceed(request)
                } catch (e: Exception) {
                    throw when (e) {
                        is SocketTimeoutException -> AppError.Timeout()
                        is UnknownHostException -> AppError.NoInternetConnection()
                        else -> e
                    }
                }
            }
            .build()

        Retrofit.Builder()
            .baseUrl("https://pokeapi.co/api/v2/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<ApiService>()
    }
    single<PokemonRepository> {
        PokemonRepositoryImpl(get(), get())
    }
    viewModel {
        PokemonListViewModel(get(),get())
    }
    viewModel {
        PokemonDetailsViewModel(get())
    }
}