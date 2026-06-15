package no.bylinnea.heve.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import no.bylinnea.heve.ui.theme.Bricolage
import no.bylinnea.heve.ui.theme.Hanken
import no.bylinnea.heve.ui.theme.HeveTheme
import no.bylinnea.heve.ui.theme.Muted

/**
 * A cream card with rounded corners and a hairline border
 */
@Composable
fun HeveCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(16.dp),
        content = content
    )
}

/**
 * A small uppercase label introducing a section ("your dough", "add a step").
 */
@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = Muted,
        fontFamily = Hanken,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        letterSpacing = 0.06.em
    )
}

// A preview lets us SEE these in the IDE without running the emulator.
@Preview(showBackground = true, backgroundColor = 0xFFF1E8D9)
@Composable
private fun ComponentsPreview() {
    HeveTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionHeader("your dough")
            Spacer(Modifier.height(8.dp))
            HeveCard {
                Text(
                    "hydration",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "72%",
                    fontFamily = Bricolage,
                    fontWeight = FontWeight.Bold,
                    fontSize = 21.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}