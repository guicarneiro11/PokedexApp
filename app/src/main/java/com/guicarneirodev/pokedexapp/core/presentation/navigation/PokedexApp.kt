package com.guicarneirodev.pokedexapp.core.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController

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