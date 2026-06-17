package no.bylinnea.heve.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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

private fun StepType.tint(): Color = when (this) {
    StepType.INGREDIENTS, StepType.BAKE -> Honey
    StepType.PROOF -> Band
    else -> Espresso
}

@Composable
fun JourneyScreen(
    modifier: Modifier = Modifier,
) {
    var steps by remember { mutableStateOf(sampleJourney) }

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
            Text(
                text = "build your bake",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 8.dp),
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
                            isDragging = isDragging
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReorderableCollectionItemScope.StepCard(
    step: JourneyStep,
    isDragging: Boolean,
) {
    val shape = RoundedCornerShape(12.dp)
    val elevation by animateDpAsState(if (isDragging) 6.dp else 0.dp, label = "elevation")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .draggableHandle()
            .shadow(elevation, shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
            .padding(vertical = 10.dp, horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(step.type.tint())
        )
        Text(
            text = step.type.label,
            fontFamily = Hanken,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        )
        if (step.type.hasDuration) {
            Text(
                text = "${step.minutes} min",
                fontFamily = Bricolage,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 10.dp),
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun JourneySummary(totalMinutes: Int) {
    val hours = totalMinutes / 60
    val mins = totalMinutes % 60
    val totalText = if (hours > 0) "$hours h $mins m" else "$mins m"
    val readyBy = remember(totalMinutes) {
        LocalTime.now().plusMinutes(totalMinutes.toLong())
            .format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Espresso)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text("total time", fontFamily = Hanken, fontSize = 12.sp, color = CreamHoney)
            Text(totalText, fontFamily = Bricolage, fontWeight = FontWeight.Bold,
                fontSize = 18.sp, color = SurfaceCream)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("ready by", fontFamily = Hanken, fontSize = 12.sp, color = CreamHoney)
            Text("~$readyBy", fontFamily = Bricolage, fontWeight = FontWeight.Bold,
                fontSize = 18.sp, color = SurfaceCream)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun JourneyScreenPreview() {
    HeveTheme {
        JourneyScreen()
    }
}