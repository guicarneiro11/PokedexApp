package com.guicarneirodev.pokedexapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guicarneirodev.pokedexapp.core.domain.model.Pokemon
import com.guicarneirodev.pokedexapp.features.list.presentation.components.PokemonItem
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonItemTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysCorrectPokemonInfo() {
        val pokemon = Pokemon(1, "bulbasaur", "url")
        var clicked = false

        composeRule.setContent {
            PokemonItem(
                pokemon = pokemon,
                onClick = { clicked = true }
            )
        }

        composeRule.onNodeWithText("#1").assertExists()
        composeRule.onNodeWithText("Bulbasaur").assertExists()

        composeRule.onNode(hasClickAction()).performClick()
        assertTrue(clicked)
    }
}