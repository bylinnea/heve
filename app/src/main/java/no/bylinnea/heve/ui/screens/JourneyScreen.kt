package no.bylinnea.heve.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.bylinnea.heve.model.JourneyStep
import no.bylinnea.heve.model.StepType
import no.bylinnea.heve.model.sampleJourney
import no.bylinnea.heve.ui.components.SectionHeader
import no.bylinnea.heve.ui.theme.Band
import no.bylinnea.heve.ui.theme.Bricolage
import no.bylinnea.heve.ui.theme.CreamHoney
import no.bylinnea.heve.ui.theme.Espresso
import no.bylinnea.heve.ui.theme.Hanken
import no.bylinnea.heve.ui.theme.HeveTheme
import no.bylinnea.heve.ui.theme.Honey
import no.bylinnea.heve.ui.theme.SurfaceCream
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import no.bylinnea.heve.ui.theme.Muted

private fun StepType.tint(): Color = when (this) {
    StepType.INGREDIENTS, StepType.BAKE -> Honey
    StepType.PROOF -> Band
    else -> Espresso
}
private fun formatMinutes(total: Int): String {
    val h = total / 60
    val m = total % 60
    return when {
        h > 0 && m > 0 -> "$h h $m min"
        h > 0 -> "$h h"
        else -> "$m min"
    }
}

private val paletteTypes = listOf(
    StepType.KNEAD, StepType.PROOF, StepType.FOLD,
    StepType.PRESHAPE, StepType.SHAPE, StepType.BAKE,
)

@Composable
fun JourneyScreen(
    modifier: Modifier = Modifier,
) {
    var steps by remember { mutableStateOf(sampleJourney) }
    var editingStepId by remember { mutableStateOf<Long?>(null) }
    val editingStep = steps.firstOrNull { it.id == editingStepId }

    val lazyListState = rememberLazyListState()
    val reorderableState = rememberReorderableLazyListState(lazyListState) { from, to ->
        steps = steps.toMutableList().apply {
            add(to.index, removeAt(from.index))
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            JourneySummary(totalMinutes = steps.sumOf { it.minutes })
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "build your bake",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "${steps.size} steps",
                    fontFamily = Hanken,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            SectionHeader(
                text = "add a step",
                modifier = Modifier.padding(start = 22.dp, end = 22.dp, bottom = 8.dp),
            )
            StepPalette(
                modifier = Modifier.padding(start = 22.dp, end = 22.dp, bottom = 16.dp),
                onAdd = { type ->
                    val newId = (steps.maxOfOrNull { it.id } ?: 0L) + 1
                    steps = steps + JourneyStep(newId, type)
                },
            )
            SectionHeader(
                text = "your sequence",
                modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 8.dp, bottom = 8.dp),
            )
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(start = 22.dp, end = 22.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(steps, key = { it.id }) { step ->
                    ReorderableItem(reorderableState, key = step.id) { isDragging ->
                        StepCard(
                            step = step,
                            isDragging = isDragging,
                            onClick = { editingStepId = step.id },
                            onDelete = { steps = steps.filterNot { it.id == step.id } },
                        )
                    }
                }
            }
        }
    }
    if (editingStep != null) {
        DurationEditSheet(
            step = editingStep,
            onMinutesChange = { newMin ->
                steps = steps.map { if (it.id == editingStep.id) it.copy(minutes = newMin) else it }
            },
            onDismiss = { editingStepId = null },
        )
    }
}

@Composable
private fun ReorderableCollectionItemScope.StepCard(
    step: JourneyStep,
    isDragging: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    val elevation by animateDpAsState(if (isDragging) 6.dp else 0.dp, label = "elevation")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation, shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.draggableHandle().size(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            DragBars()
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(step.type.tint())
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable(enabled = step.type.hasDuration, onClick = onClick)
                .padding(start = 12.dp, top = 4.dp, bottom = 4.dp),
        ) {
            Text(
                text = step.type.label,
                fontFamily = Hanken,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (step.type.hasDuration) {
                Text(
                    text = formatMinutes(step.minutes),
                    fontFamily = Hanken,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "×",
                fontFamily = Hanken,
                fontSize = 20.sp,
                color = Muted,
            )
        }
    }
}
@Composable
private fun DragBars() {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(Muted)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DurationEditSheet(
    step: JourneyStep,
    onMinutesChange: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 22.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = step.type.label,
                fontFamily = Hanken,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                MiniStepButton(isPlus = false) {
                    onMinutesChange((step.minutes - step.type.stepMinutes).coerceAtLeast(1))
                }
                Text(
                    text = "${step.minutes} min",
                    fontFamily = Bricolage,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MiniStepButton(isPlus = true) {
                    onMinutesChange((step.minutes + step.type.stepMinutes).coerceAtMost(600))
                }
            }
        }
    }
}
@Composable
private fun MiniStepButton(isPlus: Boolean, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .border(1.5.dp, c, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(width = 8.dp, height = 2.dp).clip(RoundedCornerShape(1.dp)).background(c))
        if (isPlus) {
            Box(Modifier.size(width = 2.dp, height = 8.dp).clip(RoundedCornerShape(1.dp)).background(c))
        }
    }
}
@Composable
private fun JourneySummary(totalMinutes: Int) {
    val hours = totalMinutes / 60
    val mins = totalMinutes % 60
    val totalText = if (hours > 0) "$hours h $mins m" else "$mins m"

    val now = remember(totalMinutes) { LocalTime.now() }
    val fmt = remember { DateTimeFormatter.ofPattern("HH:mm") }
    val startText = now.format(fmt)
    val readyText = now.plusMinutes(totalMinutes.toLong()).format(fmt)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Espresso)
            .navigationBarsPadding()
            .padding(horizontal = 22.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        Column {
            Text("total time", fontFamily = Hanken, fontSize = 12.sp, color = CreamHoney)
            Text(totalText, fontFamily = Bricolage, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, color = SurfaceCream)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("start $startText", fontFamily = Hanken, fontSize = 12.sp, color = CreamHoney)
            Row(verticalAlignment = Alignment.CenterVertically){
                Text("ready by ", fontFamily = Hanken, fontSize = 14.sp, color = SurfaceCream)
                Text("$readyText", fontFamily = Bricolage, fontWeight = FontWeight.Bold,
                    fontSize = 20.sp, color = SurfaceCream)
            }

        }
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StepPalette(
    onAdd: (StepType) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        maxItemsInEachRow = 3,
    ) {
        paletteTypes.forEach { type ->
            StepPaletteCard(
                type = type,
                modifier = Modifier.weight(1f),
                onClick = { onAdd(type) },
            )
        }
    }
}

@Composable
private fun StepPaletteCard(
    type: StepType,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(type.tint())
        )
        Text(
            text = type.shortLabel,
            fontFamily = Hanken,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun JourneyScreenPreview() {
    HeveTheme {
        JourneyScreen()
    }
}