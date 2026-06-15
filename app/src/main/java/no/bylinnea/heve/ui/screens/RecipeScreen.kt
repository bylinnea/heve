package no.bylinnea.heve.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.bylinnea.heve.ui.components.HeveCard
import no.bylinnea.heve.ui.components.IngredientSlider
import no.bylinnea.heve.ui.theme.Bricolage
import no.bylinnea.heve.ui.theme.Espresso
import no.bylinnea.heve.ui.theme.Hanken
import no.bylinnea.heve.ui.theme.HeveTheme
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun RecipeScreen(
    modifier: Modifier = Modifier
) {
    var hydration by remember { mutableFloatStateOf(72f) }
    var salt by remember { mutableFloatStateOf(2.0f) }
    var yeast by remember { mutableFloatStateOf(0.8f) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Espresso)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "white sandwich bread",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Text(
                text = "save",
                fontFamily = Hanken,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.height(20.dp))

        HeveCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "total dough weight",
                        fontFamily = Hanken,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "makes one large loaf",
                        fontFamily = Hanken,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StepperButton(isPlus = false)
                    Text(
                        text = "900",
                        fontFamily = Bricolage,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    StepperButton(isPlus = true)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        IngredientSlider(
            label = "hydration",
            valueText = "${hydration.roundToInt()}%",
            value = hydration,
            onValueChange = { hydration = it.roundToInt().toFloat() },
            valueRange = 55f..100f,
            step = 1f,
            bandRange = 65f..80f,
            startLabel = "55%",
            bandLabel = "rustic 65–80%",
            endLabel = "100%",
        )

        IngredientSlider(
            label = "salt",
            valueText = String.format(Locale.US, "%.1f%%", salt),
            value = salt,
            onValueChange = { salt = (it * 10).roundToInt() / 10f },
            valueRange = 1.5f..2.6f,
            step = 0.1f,
            bandRange = 1.8f..2.2f,
            startLabel = "1.5%",
            bandLabel = "typical 1.8–2.2%",
            endLabel = "2.6%",
        )

        IngredientSlider(
            label = "yeast",
            valueText = String.format(Locale.US, "%.1f%%", yeast),
            value = yeast,
            onValueChange = { yeast = (it * 10).roundToInt() / 10f },
            valueRange = 0.1f..2.0f,
            step = 0.1f,
            bandRange = 0.4f..1.0f,
            startLabel = "0.1%",
            bandLabel = "slow rise 0.4–1.0%",
            endLabel = "2.0%",
        )
    }
}

@Composable
private fun StepperButton(isPlus: Boolean) {
    val markColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(1.5.dp, markColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 10.dp, height = 2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(markColor)
        )
        if (isPlus) {
            Box(
                modifier = Modifier
                    .size(width = 2.dp, height = 10.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(markColor)
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun RecipeScreenPreview() {
    HeveTheme {
        RecipeScreen()
    }
}