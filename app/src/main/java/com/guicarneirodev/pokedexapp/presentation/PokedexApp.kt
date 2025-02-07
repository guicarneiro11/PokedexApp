package com.guicarneirodev.pokedexapp.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.guicarneirodev.pokedexapp.presentation.navigation.NavGraph

@Composable
fun PokedexApp(
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    NavGraph(
        navController = navController,
        darkTheme = darkTheme,
        onThemeChange = onThemeChange
    )
}