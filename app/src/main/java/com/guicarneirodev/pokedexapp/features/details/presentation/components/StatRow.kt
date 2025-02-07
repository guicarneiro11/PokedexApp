package com.guicarneirodev.pokedexapp.features.details.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.guicarneirodev.pokedexapp.core.domain.model.Stat
import com.guicarneirodev.pokedexapp.ui.theme.warning

@Composable
fun StatRow(
    stat: Stat,
    animateProgress: Boolean,
    typeColor: Color,
    modifier: Modifier = Modifier
) {
    val progressAnimation by animateFloatAsState(
        targetValue = if (animateProgress) stat.value / 255f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ), label = ""
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stat.name.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stat.value.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = 8.dp)
        )
        LinearProgressIndicator(
            progress = { progressAnimation },
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = when {
                stat.value < 50 -> MaterialTheme.colorScheme.error
                stat.value < 100 -> MaterialTheme.colorScheme.warning
                else -> typeColor
            },
        )
    }
}