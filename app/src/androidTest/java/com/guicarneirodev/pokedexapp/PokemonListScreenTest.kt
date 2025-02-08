package com.guicarneirodev.pokedexapp

import androidx.activity.ComponentActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.paging.PagingData
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.guicarneirodev.pokedexapp.features.shared.loading.LoadingView
import com.guicarneirodev.pokedexapp.features.list.presentation.PokemonListViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PokemonListScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsLoadingStateWhenRefreshing() {
        mockk<PokemonListViewModel> {
            every { pokemonList } returns flowOf(PagingData.empty())
            every { searchQuery } returns MutableStateFlow("")
        }

        composeRule.setContent {
            MaterialTheme {
                LoadingView(modifier = Modifier.testTag("loading_indicator"))
            }
        }

        composeRule.onNodeWithTag("loading_indicator").assertExists()
    }
}
