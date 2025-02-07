package com.guicarneirodev.pokedexapp.features.list.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun ThemeToggleButton(
    darkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    IconButton(onClick = { onThemeChange(!darkTheme) }) {
        Icon(
            imageVector = if (darkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = if (darkTheme) "Switch to light theme" else "Switch to dark theme"
        )
    }
}