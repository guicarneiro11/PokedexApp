package com.guicarneirodev.pokedexapp.features.details.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guicarneirodev.pokedexapp.ui.theme.PokemonTypeIcon
import com.guicarneirodev.pokedexapp.ui.theme.getPokemonTypeColor
import androidx.compose.foundation.layout.size

@Composable
fun TypeChip(
    type: String,
    modifier: Modifier = Modifier
) {
    val typeColor = getPokemonTypeColor(type)

    Row(
        modifier = modifier
            .padding(4.dp)
            .background(
                color = typeColor,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        PokemonTypeIcon(
            type = type,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = type.replaceFirstChar { it.uppercase() },
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}