package com.guicarneirodev.pokedexapp.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PokemonTypePattern(
    typeColor: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val patternSize = 60.dp.toPx()
        val rows = (size.height / patternSize).toInt() + 1
        val cols = (size.width / patternSize).toInt() + 1

        val pattern = Path().apply {
            for (row in 0..rows) {
                for (col in 0..cols) {
                    val x = col * patternSize + (if (row % 2 == 0) patternSize / 2 else 0f)
                    val y = row * patternSize

                    addOval(
                        Rect(
                            offset = Offset(x - patternSize/4, y - patternSize/4),
                            size = Size(patternSize/2, patternSize/2)
                        )
                    )

                    moveTo(x - patternSize/4, y)
                    lineTo(x + patternSize/4, y)

                    addOval(
                        Rect(
                            offset = Offset(x - patternSize/8, y - patternSize/8),
                            size = Size(patternSize/4, patternSize/4)
                        )
                    )
                }
            }
        }

        drawPath(
            path = pattern,
            color = typeColor.copy(alpha = 0.075f),
            style = Stroke(width = 1.dp.toPx())
        )
    }
}