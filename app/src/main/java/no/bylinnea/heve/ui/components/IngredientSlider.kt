package no.bylinnea.heve.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.bylinnea.heve.ui.theme.Band
import no.bylinnea.heve.ui.theme.Bricolage
import no.bylinnea.heve.ui.theme.Hanken
import no.bylinnea.heve.ui.theme.HeveTheme
import no.bylinnea.heve.ui.theme.Honey
import no.bylinnea.heve.ui.theme.Muted
import no.bylinnea.heve.ui.theme.SurfaceCream
import no.bylinnea.heve.ui.theme.Track
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientSlider(
    label: String,
    valueText: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    step: Float,
    bandRange: ClosedFloatingPointRange<Float>,
    startLabel: String,
    bandLabel: String,
    endLabel: String,
) {
    HeveCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontFamily = Hanken,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = valueText,
                fontFamily = Bricolage,
                fontWeight = FontWeight.Bold,
                fontSize = 21.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(Modifier.height(8.dp))

        val min = valueRange.start
        val max = valueRange.endInclusive
        // M3's `steps` counts the gaps BETWEEN endpoints, so it's (span / step) − 1.
        val steps = remember(min, max, step) {
            (((max - min) / step).roundToInt() - 1).coerceAtLeast(0)
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            thumb = {
                Box(
                    Modifier
                        .size(26.dp)
                        .shadow(3.dp, CircleShape)
                        .background(SurfaceCream, CircleShape)
                        .border(3.dp, Honey, CircleShape)
                )
            },
            track = { state ->
                Canvas(
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                ) {
                    val cy = size.height / 2f
                    val inset = 13.dp.toPx()              // half the 26dp thumb
                    val usable = size.width - inset * 2f
                    fun x(v: Float) = inset + ((v - min) / (max - min)) * usable
                    drawLine(
                        Track, Offset(x(min), cy), Offset(x(max), cy),
                        strokeWidth = size.height, cap = StrokeCap.Round
                    )
                    drawLine(
                        Band, Offset(x(bandRange.start), cy), Offset(x(bandRange.endInclusive), cy),
                        strokeWidth = size.height, cap = StrokeCap.Round
                    )
                    drawLine(
                        Honey, Offset(x(min), cy), Offset(x(state.value), cy),
                        strokeWidth = size.height, cap = StrokeCap.Round
                    )
                }
            }
        )

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CaptionText(startLabel, Muted)
            CaptionText(bandLabel, MaterialTheme.colorScheme.onSurfaceVariant) // emphasised
            CaptionText(endLabel, Muted)
        }
    }
}

@Composable
private fun CaptionText(text: String, color: Color) {
    Text(
        text = text,
        fontFamily = Hanken,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = color
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF1E8D9)
@Composable
private fun IngredientSliderPreview() {
    HeveTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            IngredientSlider(
                label = "hydration",
                valueText = "72%",
                value = 72f,
                onValueChange = {},
                valueRange = 60f..90f,
                step = 1f,
                bandRange = 65f..75f,
                startLabel = "60%",
                bandLabel = "standard range",
                endLabel = "90%"
            )
        }
    }
}
