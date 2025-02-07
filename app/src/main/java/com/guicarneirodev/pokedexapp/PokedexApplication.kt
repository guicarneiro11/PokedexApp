package com.guicarneirodev.pokedexapp

import android.app.Application
import com.guicarneirodev.pokedexapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class PokedexApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PokedexApplication)
            modules(appModule)
        }
    }
}