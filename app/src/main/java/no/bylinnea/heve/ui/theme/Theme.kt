package no.bylinnea.heve.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HeveColorScheme = lightColorScheme(
    background       = BgTan,        // the screen behind everything
    surface          = SurfaceCream, // cards, rows, sheets
    surfaceVariant   = SurfaceCream,
    primary          = Honey,        // accent: buttons, slider fills, active states
    onPrimary        = SurfaceCream, // text/icons drawn ON TOP of a honey fill
    onBackground     = Espresso,     // main text on the tan background
    onSurface        = Espresso,     // main text on cream cards
    onSurfaceVariant = Brown,        // secondary text / sub-labels
    outline          = Border,       // hairline card edges
)

@Composable
fun HeveTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HeveColorScheme,
        typography = Typography,
        content = content
    )
}