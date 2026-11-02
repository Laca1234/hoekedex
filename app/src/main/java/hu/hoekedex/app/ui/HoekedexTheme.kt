package hu.hoekedex.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val HoekedexColors = darkColorScheme(
    primary = Color(0xFFA86A45),
    onPrimary = Color(0xFF181314),
    secondary = Color(0xFF8C5A5E),
    onSecondary = Color(0xFF181314),
    background = Color(0xFF181314),
    onBackground = Color(0xFFA86A45),
    surface = Color(0xFF311414),
    onSurface = Color(0xFFA86A45),
    error = Color(0xFF4A0006),
    onError = Color(0xFFA86A45)
)

@Composable
fun HoekedexTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HoekedexColors,
        content = content
    )
}
