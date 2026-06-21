package no.bylinnea.heve.ui.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import no.bylinnea.heve.data.RecipeStore
import no.bylinnea.heve.model.ActiveBake
import no.bylinnea.heve.model.JourneyStep
import no.bylinnea.heve.model.SavedRecipe
import no.bylinnea.heve.ui.screens.BakeScreen
import no.bylinnea.heve.ui.screens.JourneyScreen
import no.bylinnea.heve.ui.screens.LibraryScreen
import no.bylinnea.heve.ui.screens.RecipeScreen

sealed class Screen {
    data object Library : Screen()
    data class Recipe(
        val name: String,
        val initialHydration: Int = 72,
        val initialTotalWeight: Int = 900,
        val initialSalt: Float = 2.0f,
        val initialYeast: Float = 0.8f,
    ) : Screen()
    data class Journey(val initialSteps: List<JourneyStep> = emptyList()) : Screen()
    data object Bake : Screen()
}

@Composable
fun HeveNavGraph(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val store = remember { RecipeStore(context) }
    val savedRecipes by store.recipes.collectAsState()
    val activeBakeId by store.activeBakeId.collectAsState()

    var backStack by remember { mutableStateOf(listOf<Screen>(Screen.Library)) }
    val current = backStack.last()

    var pendingId by remember { mutableStateOf(0L) }
    var pendingName by remember { mutableStateOf("") }
    var pendingTotalWeight by remember { mutableIntStateOf(900) }
    var pendingHydration by remember { mutableIntStateOf(72) }
    var pendingSalt by remember { mutableFloatStateOf(2.0f) }
    var pendingYeast by remember { mutableFloatStateOf(0.8f) }
    var pendingSteps by remember { mutableStateOf(listOf<JourneyStep>()) }

    fun saveRecipe() {
        if (pendingId == 0L) pendingId = System.currentTimeMillis()
        store.save(
            SavedRecipe(
                id = pendingId,
                name = pendingName,
                totalWeight = pendingTotalWeight,
                hydrationPct = pendingHydration,
                saltPct = pendingSalt,
                yeastPct = pendingYeast,
                steps = pendingSteps,
            )
        )
    }

    val activeBake: ActiveBake? = activeBakeId?.let { id ->
        savedRecipes.firstOrNull { it.id == id }?.let { recipe ->
            ActiveBake(
                recipeName = recipe.name,
                currentStep = recipe.steps.firstOrNull()?.type?.label ?: "",
                remainingMinutes = recipe.steps.firstOrNull()?.minutes ?: 0,
                stepProgress = 0f,
            )
        }
    }

    BackHandler(enabled = backStack.size > 1) {
        backStack = backStack.dropLast(1)
    }

    when (val screen = current) {
        is Screen.Library -> LibraryScreen(
            modifier = modifier,
            activeBake = activeBake,
            recipes = savedRecipes,
            onResumeBake = {
                val id = activeBakeId
                val recipe = savedRecipes.firstOrNull { it.id == id }
                if (recipe != null) {
                    pendingId = recipe.id
                    pendingName = recipe.name
                    pendingTotalWeight = recipe.totalWeight
                    pendingHydration = recipe.hydrationPct
                    pendingSalt = recipe.saltPct
                    pendingYeast = recipe.yeastPct
                    pendingSteps = recipe.steps
                }
                backStack = backStack + Screen.Bake
            },
            onNewRecipe = { name ->
                pendingId = 0L
                pendingSteps = emptyList()
                backStack = backStack + Screen.Recipe(name)
            },
            onRecipeClick = { recipe ->
                pendingId = recipe.id
                pendingName = recipe.name
                pendingTotalWeight = recipe.totalWeight
                pendingHydration = recipe.hydrationPct
                pendingSalt = recipe.saltPct
                pendingYeast = recipe.yeastPct
                pendingSteps = recipe.steps
                backStack = backStack + Screen.Recipe(
                    name = recipe.name,
                    initialHydration = recipe.hydrationPct,
                    initialTotalWeight = recipe.totalWeight,
                    initialSalt = recipe.saltPct,
                    initialYeast = recipe.yeastPct,
                )
            },
            onDeleteRecipe = { recipe ->
                store.delete(recipe.id)
                if (activeBakeId == recipe.id) store.clearActiveBake()
            },
        )
        is Screen.Recipe -> key(screen) {
            RecipeScreen(
                modifier = modifier,
                name = screen.name,
                initialTotalWeight = screen.initialTotalWeight,
                initialHydration = screen.initialHydration,
                initialSalt = screen.initialSalt,
                initialYeast = screen.initialYeast,
                onNext = { totalWeight, hydrationPct, salt, yeast ->
                    pendingName = screen.name
                    pendingTotalWeight = totalWeight
                    pendingHydration = hydrationPct
                    pendingSalt = salt
                    pendingYeast = yeast
                    backStack = backStack + Screen.Journey(pendingSteps)
                },
            )
        }
        is Screen.Journey -> JourneyScreen(
            modifier = modifier,
            initialSteps = screen.initialSteps,
            onSave = { steps ->
                pendingSteps = steps
                saveRecipe()
            },
            onStartBake = { steps ->
                pendingSteps = steps
                saveRecipe()
                store.setActiveBake(pendingId)
                backStack = backStack + Screen.Bake
            },
        )
        is Screen.Bake -> BakeScreen(
            modifier = modifier,
            steps = pendingSteps,
            onSave = { saveRecipe() },
            onFinish = {
                store.clearActiveBake()
                backStack = listOf(Screen.Library)
            },
            onHome = { backStack = listOf(Screen.Library) },
        )
    }
}
