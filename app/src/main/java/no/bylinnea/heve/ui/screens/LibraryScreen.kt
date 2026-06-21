package no.bylinnea.heve.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import no.bylinnea.heve.model.ActiveBake
import no.bylinnea.heve.model.SavedRecipe
import no.bylinnea.heve.ui.theme.Bricolage
import no.bylinnea.heve.ui.theme.CreamHoney
import no.bylinnea.heve.ui.theme.Espresso
import no.bylinnea.heve.ui.theme.Hanken
import no.bylinnea.heve.ui.theme.HeveTheme
import no.bylinnea.heve.ui.theme.Honey
import no.bylinnea.heve.ui.theme.Muted
import no.bylinnea.heve.ui.theme.SurfaceCream
import no.bylinnea.heve.ui.theme.Track

private fun formatRemaining(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return when {
        h > 0 && m > 0 -> "${h}h ${m}m left"
        h > 0           -> "${h}h left"
        else            -> "${m}m left"
    }
}

@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    activeBake: ActiveBake? = null,
    recipes: List<SavedRecipe> = emptyList(),
    onResumeBake: () -> Unit = {},
    onNewRecipe: (String) -> Unit = {},
    onRecipeClick: (SavedRecipe) -> Unit = {},
    onDeleteRecipe: (SavedRecipe) -> Unit = {},
) {
    var showNewDialog by remember { mutableStateOf(false) }

    if (showNewDialog) {
        NewRecipeDialog(
            onConfirm = { name ->
                showNewDialog = false
                onNewRecipe(name)
            },
            onDismiss = { showNewDialog = false },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 22.dp, end = 22.dp, top = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text("library", style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "+ new",
                        fontFamily = Hanken,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Honey,
                        modifier = Modifier.clickable { showNewDialog = true },
                    )
                }
            }

            if (activeBake != null) {
                item {
                    ResumeBakeBanner(
                        bake = activeBake,
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp),
                        onClick = onResumeBake,
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            if (recipes.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("no recipes yet", fontFamily = Hanken, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Muted)
                        Text("tap + new to create your first", fontFamily = Hanken, fontSize = 13.sp, color = Muted)
                    }
                }
            } else {
                items(recipes, key = { it.id }) { recipe ->
                    RecipeRow(
                        recipe = recipe,
                        modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp),
                        onClick = { onRecipeClick(recipe) },
                        onDelete = { onDeleteRecipe(recipe) },
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun ResumeBakeBanner(
    bake: ActiveBake,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Espresso)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = "resume bake",
            fontFamily = Hanken,
            fontSize = 12.sp,
            color = CreamHoney,
            letterSpacing = 0.06.em,
        )
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Honey),
                contentAlignment = Alignment.Center,
            ) {
                Text("▶", fontFamily = Hanken, fontSize = 12.sp, color = SurfaceCream)
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = bake.recipeName,
                    fontFamily = Bricolage,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SurfaceCream,
                )
                Text(
                    text = "${bake.currentStep} · ${formatRemaining(bake.remainingMinutes)}",
                    fontFamily = Hanken,
                    fontSize = 12.sp,
                    color = CreamHoney,
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { bake.stepProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = Honey,
            trackColor = SurfaceCream.copy(alpha = 0.15f),
            strokeCap = StrokeCap.Round,
        )
    }
}

@Composable
private fun RecipeRow(
    recipe: SavedRecipe,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceCream)
            .border(1.dp, Track, shape)
            .clickable(onClick = onClick)
            .padding(start = 14.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Espresso)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = recipe.name,
                fontFamily = Hanken,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "${recipe.hydrationPct}% hydration · ${recipe.stepCount} steps · ~${recipe.durationHours}h",
                fontFamily = Hanken,
                fontSize = 12.sp,
                color = Muted,
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center,
        ) {
            Text("×", fontFamily = Hanken, fontSize = 20.sp, color = Muted)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewRecipeDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val trimmed = name.trim()

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                "name your recipe",
                fontFamily = Bricolage,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
            )
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = {
                    Text(
                        "e.g. country sourdough",
                        fontFamily = Hanken,
                        fontSize = 15.sp,
                        color = Muted,
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (trimmed.isNotEmpty()) onConfirm(trimmed)
                }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Honey,
                    unfocusedBorderColor = Track,
                    cursorColor = Honey,
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = Hanken,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Track)
                        .clickable(onClick = onDismiss)
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "cancel",
                        fontFamily = Hanken,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Muted,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (trimmed.isEmpty()) Track else Espresso)
                        .clickable(enabled = trimmed.isNotEmpty()) { onConfirm(trimmed) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "create",
                        fontFamily = Hanken,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = if (trimmed.isEmpty()) Muted else SurfaceCream,
                    )
                }
            }
        }
    }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun LibraryScreenPreview() {
    HeveTheme {
        LibraryScreen()
    }
}
