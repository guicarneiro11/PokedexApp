package com.guicarneirodev.pokedexapp

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guicarneirodev.pokedexapp.core.domain.model.Move
import com.guicarneirodev.pokedexapp.features.details.presentation.components.MovesSection
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoveSectionTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsFiveMovesByDefault() {
        val moves = List(10) { index ->
            Move(name = "move$index", type = "normal")
        }

        composeRule.setContent {
            MaterialTheme {
                MovesSection(moves = moves, typeColor = Color.Red)
            }
        }

        composeRule.onNodeWithText("Show More (5)").assertExists()

        for (i in 0..4) {
            composeRule.onNodeWithText("move$i".replaceFirstChar { it.uppercase() })
                .assertExists()
        }

        composeRule.onNodeWithText("move5".replaceFirstChar { it.uppercase() })
            .assertDoesNotExist()
    }

    @Test
    fun `expands to show all moves when clicking show more`() {
        val moves = List(10) { index -> Move(name = "move$index", type = "normal") }

        composeRule.setContent {
            MaterialTheme {
                MovesSection(moves = moves, typeColor = Color.Red)
            }
        }

        // Verificar estado inicial
        composeRule.onNodeWithText("move5").assertDoesNotExist()

        // Clicar no botão e verificar expansão
        composeRule.onNodeWithText("Show More (5)").performClick()

        // Verificar se todos os moves são mostrados
        for (i in 5..9) {
            composeRule.onNodeWithText("move$i".replaceFirstChar { it.uppercase() })
                .assertExists()
        }
    }
}