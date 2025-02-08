package com.guicarneirodev.pokedexapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guicarneirodev.pokedexapp.features.list.presentation.components.PokemonSearchBar
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonSearchBarTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun filtersNonAlphabeticCharacters() {
        var query = ""
        composeRule.setContent {
            PokemonSearchBar(
                query = query,
                onQueryChange = { query = it }
            )
        }

        composeRule.onNode(hasSetTextAction()).performTextInput("pikachu123!@#")
        assertEquals("pikachu", query)
    }
}