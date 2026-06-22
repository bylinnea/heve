package no.bylinnea.heve.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import no.bylinnea.heve.model.JourneyStep
import no.bylinnea.heve.model.sampleJourney
import no.bylinnea.heve.notification.StepAlarmScheduler
import no.bylinnea.heve.ui.theme.Bricolage
import no.bylinnea.heve.ui.theme.CreamHoney
import no.bylinnea.heve.ui.theme.Espresso
import no.bylinnea.heve.ui.theme.Hanken
import no.bylinnea.heve.ui.theme.HeveTheme
import no.bylinnea.heve.ui.theme.Honey
import no.bylinnea.heve.ui.theme.Muted
import no.bylinnea.heve.ui.theme.SurfaceCream
import no.bylinnea.heve.ui.theme.Track
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

private fun formatTime(seconds: Int): String {
    val neg = seconds < 0
    val s = abs(seconds)
    return (if (neg) "-" else "") + "${s / 60}:${(s % 60).toString().padStart(2, '0')}"
}

@Composable
fun BakeScreen(
    modifier: Modifier = Modifier,
    steps: List<JourneyStep> = emptyList(),
    onSave: () -> Unit = {},
    onFinish: () -> Unit = {},
    onHome: () -> Unit = {},
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var remainingSeconds by remember { mutableIntStateOf(0) }
    var bakeComplete by remember { mutableStateOf(false) }

    val activeSteps = steps.ifEmpty { sampleJourney }
    val current = activeSteps[currentIndex]
    val nextStep = activeSteps.getOrNull(currentIndex + 1)
    val isTimed = current.type.hasDuration
    val totalSeconds = current.minutes * 60
    val overdue = remainingSeconds < 0
    val progress = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f

    val context = LocalContext.current
    val ding = remember {
        RingtoneManager.getRingtone(
            context,
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    DisposableEffect(Unit) {
        onDispose { StepAlarmScheduler.cancel(context) }
    }

    LaunchedEffect(currentIndex) {
        val step = activeSteps[currentIndex]
        StepAlarmScheduler.cancel(context)
        if (!step.type.hasDuration) return@LaunchedEffect
        StepAlarmScheduler.schedule(context, step.type.label, step.minutes)
        remainingSeconds = step.minutes * 60
        while (true) {
            delay(1000)
            remainingSeconds--
            if (remainingSeconds == 0) ding.play()
        }
    }

    val advance: () -> Unit = {
        if (currentIndex < activeSteps.lastIndex) currentIndex++
        else bakeComplete = true
    }

    val nextStartsAt = if (isTimed) {
        LocalTime.now().plusSeconds(remainingSeconds.coerceAtLeast(0).toLong())
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    } else null

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        if (bakeComplete) {
            BakeCompleteScreen(
                innerPadding = innerPadding,
                onSave = { onSave(); onFinish() },
                onDone = onFinish,
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "‹",
                        fontFamily = Hanken,
                        fontSize = 24.sp,
                        color = Muted,
                        modifier = Modifier.clickable(onClick = onHome),
                    )
                    Text("baking", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "step ${currentIndex + 1} of ${activeSteps.size}",
                        fontFamily = Hanken, fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(Modifier.height(40.dp))

                if (isTimed) {
                    CountdownRing(progress, formatTime(remainingSeconds), overdue)
                } else {
                    TapWhenDonePrompt()
                }

                Spacer(Modifier.height(28.dp))

                Text(
                    current.type.label,
                    fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Spacer(Modifier.height(20.dp))
                StepDots(count = activeSteps.size, current = currentIndex)
                Spacer(Modifier.height(28.dp))

                UpNextBar(nextStep = nextStep, startsAt = nextStartsAt, onClick = advance)
            }
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
private fun UpNextBar(nextStep: JourneyStep?, startsAt: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Espresso)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(if (nextStep == null) "finish" else "up next",
                fontFamily = Hanken, fontSize = 13.sp, color = CreamHoney)
            if (nextStep != null && startsAt != null) {
                Text("at $startsAt", fontFamily = Hanken, fontSize = 12.sp, color = CreamHoney)
            }
        }
        Text(
            text = if (nextStep == null) "finish bake" else {
                val dur = if (nextStep.type.hasDuration) " · ${nextStep.minutes} min" else ""
                "${nextStep.type.label}$dur"
            },
            fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SurfaceCream,
        )
    }
}

@Composable
private fun TapWhenDonePrompt() {
    Box(modifier = Modifier.size(230.dp), contentAlignment = Alignment.Center) {
        Text(
            "tap “up next”\nwhen you're done",
            fontFamily = Hanken, fontSize = 16.sp, textAlign = TextAlign.Center, color = Muted,
        )
    }
}

@Composable
private fun BakeCompleteScreen(
    innerPadding: PaddingValues,
    onSave: () -> Unit,
    onDone: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "bake complete",
            fontFamily = Bricolage, fontWeight = FontWeight.Bold, fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "well baked!",
            fontFamily = Hanken, fontSize = 16.sp, color = Muted,
        )
        Spacer(Modifier.height(48.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                .background(Espresso)
                .clickable(onClick = onSave)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "save bake",
                fontFamily = Hanken, fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp, color = SurfaceCream,
            )
        }
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(14.dp))
                .background(Track)
                .clickable(onClick = onDone)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "done",
                fontFamily = Hanken, fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp, color = Muted,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun BakeScreenPreview() {
    HeveTheme {
        BakeScreen()
    }
}