package no.bylinnea.heve.model

data class SavedRecipe(
    val id: Long,
    val name: String,
    val totalWeight: Int,
    val hydrationPct: Int,
    val saltPct: Float,
    val yeastPct: Float,
    val steps: List<JourneyStep>,
) {
    val stepCount: Int get() = steps.size
    val durationHours: Int get() = steps.sumOf { it.minutes } / 60
}

data class ActiveBake(
    val recipeName: String,
    val currentStep: String,
    val remainingMinutes: Int,
    val stepProgress: Float,
)
