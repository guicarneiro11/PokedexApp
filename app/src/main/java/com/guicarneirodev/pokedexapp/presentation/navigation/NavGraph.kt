package com.guicarneirodev.pokedexapp.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.guicarneirodev.pokedexapp.features.details.presentation.PokemonDetailsScreen
import com.guicarneirodev.pokedexapp.features.list.presentation.PokemonListScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = "pokemon_list",
        enterTransition = { slideInHorizontally { it } },
        exitTransition = { slideOutHorizontally { -it } },
        popEnterTransition = { slideInHorizontally { -it } },
        popExitTransition = { slideOutHorizontally { it } }
    ) {
        composable("pokemon_list") {
            PokemonListScreen(
                onPokemonClick = { pokemonId ->
                    navController.navigate("pokemon_details/$pokemonId")
                },
                darkTheme = darkTheme,
                onThemeChange = onThemeChange
            )
        }
        composable(
            route = "pokemon_details/{pokemonId}",
            arguments = listOf(navArgument("pokemonId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pokemonId = backStackEntry.arguments?.getInt("pokemonId") ?: return@composable
            PokemonDetailsScreen(
                pokemonId = pokemonId,
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}