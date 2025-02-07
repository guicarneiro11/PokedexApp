package com.guicarneirodev.pokedexapp.features.details.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guicarneirodev.pokedexapp.core.domain.model.Move
import com.guicarneirodev.pokedexapp.ui.theme.getPokemonTypeColor

@Composable
fun MovesSection(
    moves: List<Move>,
    typeColor: Color,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val displayedMoves = if (expanded) moves else moves.take(5)

    Column(modifier = modifier) {
        Text(
            text = "Moves",
            style = MaterialTheme.typography.titleMedium.copy(
                color = typeColor,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(displayedMoves) { move ->
                MoveItem(
                    move = move,
                    typeColor = getPokemonTypeColor(move.type)
                )
            }
            if (!expanded && moves.size > 5) {
                item {
                    TextButton(
                        onClick = { expanded = true }
                    ) {
                        Text("Show More (${moves.size - 5})")
                    }
                }
            }
        }
    }
}