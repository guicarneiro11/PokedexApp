package com.guicarneirodev.pokedexapp

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guicarneirodev.pokedexapp.core.domain.error.AppError
import com.guicarneirodev.pokedexapp.core.domain.model.PokemonDetails
import com.guicarneirodev.pokedexapp.core.domain.model.Stat
import com.guicarneirodev.pokedexapp.features.details.presentation.components.PokemonDetailsContent
import com.guicarneirodev.pokedexapp.features.shared.error.ErrorView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonDetailsScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsErrorStateCorrectly() {
        composeRule.setContent {
            MaterialTheme {
                ErrorView(
                    error = AppError.Network("Check your connection"),
                    onRetry = {}
                )
            }
        }

        composeRule.onNodeWithText("Check your connection").assertExists()
    }

    @Test
    fun showsSuccessStateCorrectly() {
        val pokemon = PokemonDetails(
            id = 1,
            name = "bulbasaur",
            height = 0.7,
            weight = 6.9,
            types = listOf("grass"),
            stats = listOf(Stat("hp", 45)),
            imageUrl = "url",
            abilities = listOf(),
            moves = listOf()
        )

        composeRule.setContent {
            MaterialTheme {
                PokemonDetailsContent(pokemon = pokemon)
            }
        }

        composeRule.onNodeWithText("#1 Bulbasaur").assertExists()
    }
}