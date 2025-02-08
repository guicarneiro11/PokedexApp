package com.guicarneirodev.pokedexapp.features.details.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guicarneirodev.pokedexapp.core.domain.model.PokemonDetails
import com.guicarneirodev.pokedexapp.core.presentation.theme.getPokemonTypeColor
import com.guicarneirodev.pokedexapp.features.shared.image.PokemonImage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PokemonDetailsContent(
    pokemon: PokemonDetails,
    modifier: Modifier = Modifier
) {
    var contentVisible by remember { mutableStateOf(false) }
    val mainTypeColor = remember(pokemon) {
        getPokemonTypeColor(pokemon.types.first())
    }

    LaunchedEffect(Unit) {
        contentVisible = true
    }

    Box(modifier = modifier.fillMaxSize()) {
        PokemonTypePattern(
            typeColor = mainTypeColor,
            modifier = Modifier.fillMaxSize()
        )

        AnimatedVisibility(
            visible = contentVisible,
            enter = fadeIn(
                animationSpec = tween(
                    durationMillis = 500
                )
            ) + expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween(durationMillis = 500)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                mainTypeColor.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                            )
                        )
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PokemonImage(
                    imageUrl = pokemon.imageUrl,
                    pokemonName = pokemon.name,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .scale(
                            animateFloatAsState(
                                targetValue = if (contentVisible) 1f else 0.8f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                ), label = ""
                            ).value
                        )
                )

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 300
                        )
                    ) + slideInVertically()
                ) {
                    Text(
                        text = "#${pokemon.id} ${pokemon.name.replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = mainTypeColor
                        ),
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 400
                        )
                    ) + slideInHorizontally()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Height: ${pokemon.height} m",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Weight: ${pokemon.weight} kg",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 500
                        )
                    )
                ) {
                    Text(
                        text = "Types",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = mainTypeColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    maxItemsInEachRow = 3
                ) {
                    pokemon.types.forEachIndexed { index, type ->
                        AnimatedVisibility(
                            visible = contentVisible,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = 600 + (index * 100)
                                )
                            ) + slideInHorizontally(
                                animationSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = 600 + (index * 100)
                                ),
                                initialOffsetX = { if (index % 2 == 0) -it else it }
                            )
                        ) {
                            TypeChip(type = type)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 700
                        )
                    )
                ) {
                    Text(
                        text = "Stats",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = mainTypeColor,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    pokemon.stats.forEachIndexed { index, stat ->
                        AnimatedVisibility(
                            visible = contentVisible,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = 800 + (index * 100)
                                )
                            ) + slideInHorizontally(
                                animationSpec = tween(
                                    durationMillis = 500,
                                    delayMillis = 800 + (index * 100)
                                )
                            )
                        ) {
                            StatRow(
                                stat = stat,
                                animateProgress = contentVisible,
                                typeColor = mainTypeColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 900
                        )
                    )
                ) {
                    AbilitiesSection(
                        abilities = pokemon.abilities,
                        typeColor = mainTypeColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = 1000
                        )
                    )
                ) {
                    MovesSection(
                        moves = pokemon.moves,
                        typeColor = mainTypeColor
                    )
                }
            }
        }
    }
}