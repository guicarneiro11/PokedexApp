package com.guicarneirodev.pokedexapp.features.details.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.guicarneirodev.pokedexapp.core.domain.model.Ability

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AbilitiesSection(
    abilities: List<Ability>,
    typeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Abilities",
            style = MaterialTheme.typography.titleMedium.copy(
                color = typeColor,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            abilities.forEach { ability ->
                AbilityChip(
                    ability = ability,
                    typeColor = typeColor
                )
            }
        }
    }
}