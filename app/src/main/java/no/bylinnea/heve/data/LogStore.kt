package no.bylinnea.heve.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.bylinnea.heve.model.BakeLog
import no.bylinnea.heve.model.FlourType
import org.json.JSONArray
import org.json.JSONObject

class LogStore(context: Context) {

    private val prefs = context.getSharedPreferences("heve_log", Context.MODE_PRIVATE)
    private val _entries = MutableStateFlow(load())
    val entries: StateFlow<List<BakeLog>> = _entries.asStateFlow()

    fun save(entry: BakeLog) {
        val updated = listOf(entry) + _entries.value
        _entries.value = updated
        prefs.edit().putString(KEY, serialize(updated)).apply()
    }

    fun delete(id: Long) {
        val updated = _entries.value.filterNot { it.id == id }
        _entries.value = updated
        prefs.edit().putString(KEY, serialize(updated)).apply()
    }

    private fun load(): List<BakeLog> =
        prefs.getString(KEY, null)?.let { deserialize(it) } ?: emptyList()

    private companion object {
        const val KEY = "log"

        fun serialize(entries: List<BakeLog>): String = JSONArray().apply {
            entries.forEach { e ->
                put(JSONObject().apply {
                    put("id", e.id)
                    put("recipeName", e.recipeName)
                    put("flourType", e.flourType.name)
                    put("totalWeight", e.totalWeight)
                    put("totalMinutes", e.totalMinutes)
                })
            }
        }.toString()

        fun deserialize(json: String): List<BakeLog> = try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                BakeLog(
                    id = o.getLong("id"),
                    recipeName = o.getString("recipeName"),
                    flourType = runCatching { FlourType.valueOf(o.getString("flourType")) }.getOrDefault(FlourType.FLOUR_00),
                    totalWeight = o.getInt("totalWeight"),
                    totalMinutes = o.getInt("totalMinutes"),
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
