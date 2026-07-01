package no.bylinnea.heve.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.bylinnea.heve.model.FlourType
import no.bylinnea.heve.model.JourneyStep
import no.bylinnea.heve.model.SavedRecipe
import no.bylinnea.heve.model.StepType
import org.json.JSONArray
import org.json.JSONObject

class RecipeStore(context: Context) {

    private val prefs = context.getSharedPreferences("heve_recipes", Context.MODE_PRIVATE)
    private val _recipes = MutableStateFlow(load())
    val recipes: StateFlow<List<SavedRecipe>> = _recipes.asStateFlow()

    private val _activeBakeId = MutableStateFlow<Long?>(
        prefs.getLong(BAKE_KEY, -1L).takeIf { it != -1L }
    )
    val activeBakeId: StateFlow<Long?> = _activeBakeId.asStateFlow()

    fun save(recipe: SavedRecipe) {
        val updated = _recipes.value.filterNot { it.id == recipe.id } + recipe
        _recipes.value = updated
        prefs.edit().putString(KEY, serialize(updated)).apply()
    }

    fun delete(id: Long) {
        val updated = _recipes.value.filterNot { it.id == id }
        _recipes.value = updated
        prefs.edit().putString(KEY, serialize(updated)).apply()
    }

    private val _activeStepIndex = MutableStateFlow(prefs.getInt(STEP_IDX_KEY, 0))
    val activeStepIndex: StateFlow<Int> = _activeStepIndex.asStateFlow()

    private val _stepStartTime = MutableStateFlow<Long?>(
        prefs.getLong(STEP_START_KEY, -1L).takeIf { it != -1L }
    )
    val stepStartTime: StateFlow<Long?> = _stepStartTime.asStateFlow()

    fun setActiveBake(id: Long) {
        _activeBakeId.value = id
        prefs.edit().putLong(BAKE_KEY, id).apply()
    }

    fun setActiveStep(index: Int) {
        val now = System.currentTimeMillis()
        _activeStepIndex.value = index
        _stepStartTime.value = now
        prefs.edit().putInt(STEP_IDX_KEY, index).putLong(STEP_START_KEY, now).apply()
    }

    fun clearActiveBake() {
        _activeBakeId.value = null
        _activeStepIndex.value = 0
        _stepStartTime.value = null
        prefs.edit().remove(BAKE_KEY).remove(STEP_IDX_KEY).remove(STEP_START_KEY).apply()
    }

    private fun load(): List<SavedRecipe> =
        prefs.getString(KEY, null)?.let { deserialize(it) } ?: emptyList()

    private companion object {
        const val KEY = "recipes"
        const val BAKE_KEY = "active_bake_id"
        const val STEP_IDX_KEY = "active_step_index"
        const val STEP_START_KEY = "step_start_time"

        fun serialize(recipes: List<SavedRecipe>): String = JSONArray().apply {
            recipes.forEach { recipe ->
                put(JSONObject().apply {
                    put("id", recipe.id)
                    put("name", recipe.name)
                    put("totalWeight", recipe.totalWeight)
                    put("hydrationPct", recipe.hydrationPct)
                    put("saltPct", recipe.saltPct.toDouble())
                    put("yeastPct", recipe.yeastPct.toDouble())
                    put("flourType", recipe.flourType.name)
                    put("steps", JSONArray().apply {
                        recipe.steps.forEach { step ->
                            put(JSONObject().apply {
                                put("id", step.id)
                                put("type", step.type.name)
                                put("minutes", step.minutes)
                            })
                        }
                    })
                })
            }
        }.toString()

        fun deserialize(json: String): List<SavedRecipe> = try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                val stepsArr = obj.getJSONArray("steps")
                SavedRecipe(
                    id = obj.getLong("id"),
                    name = obj.getString("name"),
                    totalWeight = if (obj.has("totalWeight")) obj.getInt("totalWeight") else 900,
                    hydrationPct = obj.getInt("hydrationPct"),
                    saltPct = if (obj.has("saltPct")) obj.getDouble("saltPct").toFloat() else 2.0f,
                    yeastPct = if (obj.has("yeastPct")) obj.getDouble("yeastPct").toFloat() else 0.8f,
                    flourType = if (obj.has("flourType")) runCatching { FlourType.valueOf(obj.getString("flourType")) }.getOrDefault(FlourType.FLOUR_00) else FlourType.FLOUR_00,
                    steps = (0 until stepsArr.length()).map { j ->
                        val s = stepsArr.getJSONObject(j)
                        JourneyStep(
                            id = s.getLong("id"),
                            type = StepType.valueOf(s.getString("type")),
                            minutes = s.getInt("minutes"),
                        )
                    },
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
