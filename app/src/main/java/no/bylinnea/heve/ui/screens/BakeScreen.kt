package no.bylinnea.heve.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.bylinnea.heve.model.JourneyStep
import no.bylinnea.heve.model.sampleJourney
import no.bylinnea.heve.ui.theme.Bricolage
import no.bylinnea.heve.ui.theme.CreamHoney
import no.bylinnea.heve.ui.theme.Espresso
import no.bylinnea.heve.ui.theme.Hanken
import no.bylinnea.heve.ui.theme.HeveTheme
import no.bylinnea.heve.ui.theme.Honey
import no.bylinnea.heve.ui.theme.Muted
import no.bylinnea.heve.ui.theme.SurfaceCream
import no.bylinnea.heve.ui.theme.Track
import kotlin.math.abs

private fun formatTime(seconds: Int): String {
    val neg = seconds < 0
    val s = abs(seconds)
    return (if (neg) "-" else "") + "${s / 60}:${(s % 60).toString().padStart(2, '0')}"
}

@Composable
fun BakeScreen(
    modifier: Modifier = Modifier,
    steps: List<JourneyStep> = sampleJourney,
) {
    val currentIndex = 2
    val current = steps[currentIndex]
    val totalSeconds = current.minutes * 60
    val remainingSeconds = 134
    val overdue = remainingSeconds < 0
    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f
    val nextStep = steps.getOrNull(currentIndex + 1)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text("baking", style = MaterialTheme.typography.titleLarge)
                Text(
                    text = "step ${currentIndex + 1} of ${steps.size}",
                    fontFamily = Hanken, fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(Modifier.weight(1f))

            CountdownRing(progress = progress, timeText = formatTime(remainingSeconds), overdue = overdue)

            Spacer(Modifier.height(28.dp))

            Text(
                text = current.type.label,
                fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )

            Spacer(Modifier.height(20.dp))

            StepDots(count = steps.size, current = currentIndex)

            Spacer(Modifier.height(28.dp))

            UpNextBar(nextStep = nextStep, startsAt = "09:14")

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun CountdownRing(progress: Float, timeText: String, overdue: Boolean) {
    Box(modifier = Modifier.size(230.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 14.dp.toPx()
            val d = size.minDimension - stroke
            val topLeft = Offset((size.width - d) / 2f, (size.height - d) / 2f)
            val arcSize = Size(d, d)
            drawArc(
                color = Track, startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(stroke, cap = StrokeCap.Round),
            )
            drawArc(
                color = Honey, startAngle = -90f, sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false, topLeft = topLeft, size = arcSize,
                style = Stroke(stroke, cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = timeText,
                fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 48.sp,
                color = if (overdue) Honey else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = if (overdue) "overdue" else "remaining",
                fontFamily = Hanken, fontSize = 14.sp, color = Muted,
            )
        }
    }
}

@Composable
private fun StepDots(count: Int, current: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(count) { i ->
            val size = if (i == current) 9.dp else 7.dp
            val color = when {
                i == current -> Espresso
                i < current -> Honey
                else -> Track
            }
            Box(Modifier.size(size).clip(CircleShape).background(color))
        }
    }
}

@Composable
private fun UpNextBar(nextStep: JourneyStep?, startsAt: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Espresso)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("up next", fontFamily = Hanken, fontSize = 13.sp, color = CreamHoney)
            if (nextStep != null) {
                Text("at $startsAt", fontFamily = Hanken, fontSize = 12.sp, color = CreamHoney)
            }
        }
        Text(
            text = if (nextStep == null) "last step" else {
                val dur = if (nextStep.type.hasDuration) " · ${nextStep.minutes} min" else ""
                "${nextStep.type.label}$dur"
            },
            fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 15.sp,
            color = SurfaceCream,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun BakeScreenPreview() {
    HeveTheme {
        BakeScreen()
    }
}