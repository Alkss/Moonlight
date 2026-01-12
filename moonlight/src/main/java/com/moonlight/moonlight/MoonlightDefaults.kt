package com.moonlight.moonlight

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class MoonlightColors(
    val overlayColor: Color = Color.Black.copy(alpha = 0.7f),
    val containerColor: Color = Color.White,
    val titleContentColor: Color = Color.Black,
    val textContentColor: Color = Color.DarkGray,
    val actionButtonContainerColor: Color = Color(0xFF6200EE),
    val actionButtonContentColor: Color = Color.White,
    val indicatorColor: Color = Color.Gray
)

data class MoonlightTypography(
    val titleStyle: TextStyle = TextStyle.Default,
    val textStyle: TextStyle = TextStyle.Default,
    val actionButtonStyle: TextStyle = TextStyle.Default,
    val indicatorStyle: TextStyle = TextStyle.Default
)

data object MoonlightDefaults {
    @Composable
    fun colors(
        overlayColor: Color = Color.Black.copy(alpha = 0.7f),
        containerColor: Color = MaterialTheme.colorScheme.surface,
        titleContentColor: Color = MaterialTheme.colorScheme.onSurface,
        textContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        actionButtonContainerColor: Color = MaterialTheme.colorScheme.primary,
        actionButtonContentColor: Color = MaterialTheme.colorScheme.onPrimary,
        indicatorColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
    ): MoonlightColors = MoonlightColors(
        overlayColor = overlayColor,
        containerColor = containerColor,
        titleContentColor = titleContentColor,
        textContentColor = textContentColor,
        actionButtonContainerColor = actionButtonContainerColor,
        actionButtonContentColor = actionButtonContentColor,
        indicatorColor = indicatorColor
    )

    @Composable
    fun typography(
        titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
        textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
        actionButtonStyle: TextStyle = MaterialTheme.typography.labelLarge,
        indicatorStyle: TextStyle = MaterialTheme.typography.labelMedium
    ): MoonlightTypography = MoonlightTypography(
        titleStyle = titleStyle,
        textStyle = textStyle,
        actionButtonStyle = actionButtonStyle,
        indicatorStyle = indicatorStyle
    )
}

