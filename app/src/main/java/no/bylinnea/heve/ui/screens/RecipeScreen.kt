package no.bylinnea.heve.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import no.bylinnea.heve.ui.components.CircleStepButton
import no.bylinnea.heve.ui.components.HeveCard
import no.bylinnea.heve.ui.components.IngredientSlider
import no.bylinnea.heve.ui.theme.Bricolage
import no.bylinnea.heve.ui.theme.CreamHoney
import no.bylinnea.heve.ui.theme.Espresso
import no.bylinnea.heve.ui.theme.Hanken
import no.bylinnea.heve.ui.theme.HeveTheme
import no.bylinnea.heve.ui.theme.SurfaceCream
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun RecipeScreen(
    modifier: Modifier = Modifier,
    name: String = "new recipe",
    initialTotalWeight: Int = 900,
    initialHydration: Int = 72,
    initialSalt: Float = 2.0f,
    initialYeast: Float = 0.8f,
    onNext: (totalWeight: Int, hydrationPct: Int, salt: Float, yeast: Float) -> Unit = { _, _, _, _ -> },
) {
    var totalWeight by remember(initialTotalWeight) { mutableIntStateOf(initialTotalWeight) }
    var hydration by remember(initialHydration) { mutableFloatStateOf(initialHydration.toFloat()) }
    var salt by remember(initialSalt) { mutableFloatStateOf(initialSalt) }
    var yeast by remember(initialYeast) { mutableFloatStateOf(initialYeast) }

    val dough = bakersBreakdown(totalWeight, hydration, salt, yeast)
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Box(Modifier.padding(horizontal = 22.dp, vertical = 16.dp)) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onNext(totalWeight, hydration.roundToInt(), salt, yeast)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(
                        text = "next",
                        fontFamily = Hanken,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Espresso)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge
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
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        CircleStepButton(
                            isPlus = false,
                            onClick = { totalWeight = (totalWeight - 50).coerceAtLeast(100) }
                        )
                        EditableWeight(
                            value = totalWeight,
                            onValueChange = { totalWeight = it }
                        )
                        CircleStepButton(
                            isPlus = true,
                            onClick = { totalWeight = (totalWeight + 50).coerceAtMost(5000) }
                        )
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
            Spacer(Modifier.height(12.dp))
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
            Spacer(Modifier.height(12.dp))
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
            Spacer(Modifier.height(16.dp))
            ResultPanel(dough)
        }
    }
}

@Composable
private fun EditableWeight(
    value: Int,
    onValueChange: (Int) -> Unit,
) {
    var editing by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(value.toString()) }
    var hasBeenFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(value, editing) {
        if (!editing) text = value.toString()
    }

    fun commit() {
        val parsed = text.toIntOrNull() ?: value
        onValueChange(parsed.coerceIn(100, 5000))
        editing = false
    }

    val numberStyle = TextStyle(
        fontFamily = Bricolage,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
    )

    if (editing) {
        BasicTextField(
            value = text,
            onValueChange = { typed -> text = typed.filter { it.isDigit() }.take(4) },
            textStyle = numberStyle,
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                commit()
                focusManager.clearFocus()
            }),
            modifier = Modifier
                .width(80.dp)
                .focusRequester(focusRequester)
                .onFocusChanged { state ->
                    if (state.isFocused) hasBeenFocused = true
                    else if (hasBeenFocused) commit()
                }
        )
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    } else {
        Text(
            text = "$value",
            style = numberStyle,
            modifier = Modifier
                .width(80.dp)
                .clickable {
                    text = value.toString()
                    hasBeenFocused = false
                    editing = true
                }
        )
    }
}
private data class DoughBreakdown(
    val flour: Int,
    val water: Int,
    val salt: Int,
    val yeast: Int,
)

/**
 * Baker's percentages: flour is 100%, everything else is a percentage of it.
 * flour = total ÷ (1 + hydration% + salt% + yeast%), then each weight is
 * flour × its own percentage. Rounded to whole grams, so the four can sum to
 * a gram or two off the target.
 */
private fun bakersBreakdown(
    totalWeight: Int,
    hydration: Float,
    salt: Float,
    yeast: Float,
): DoughBreakdown {
    val divisor = 1f + (hydration + salt + yeast) / 100f
    val flour = totalWeight / divisor
    return DoughBreakdown(
        flour = flour.roundToInt(),
        water = (flour * hydration / 100f).roundToInt(),
        salt = (flour * salt / 100f).roundToInt(),
        yeast = (flour * yeast / 100f).roundToInt(),
    )
}
@Composable
private fun ResultPanel(dough: DoughBreakdown) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Espresso)
            .padding(16.dp)
    ) {
        Text(
            text = "your dough".uppercase(),
            fontFamily = Hanken,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 0.08.em,
            color = CreamHoney
        )
        Spacer(Modifier.height(10.dp))

        val rows = listOf(
            "flour" to dough.flour,
            "water" to dough.water,
            "salt" to dough.salt,
            "yeast" to dough.yeast,
        )
        rows.forEachIndexed { index, (label, grams) ->
            if (index > 0) {
                HorizontalDivider(color = SurfaceCream.copy(alpha = 0.12f))
            }
            ResultRow(label, grams)
        }
    }
}
@Composable
private fun ResultRow(label: String, grams: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontFamily = Hanken,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = CreamHoney
        )
        Text(
            text = "$grams g",
            fontFamily = Bricolage,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = SurfaceCream
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun RecipeScreenPreview() {
    HeveTheme {
        RecipeScreen()
    }
}