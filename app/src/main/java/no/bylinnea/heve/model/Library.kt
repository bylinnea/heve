package no.bylinnea.heve.model

enum class FlourType(
    val label: String,
    val hydrationBand: ClosedFloatingPointRange<Float>,
    val hydrationBandLabel: String,
) {
    FLOUR_00("00", 62f..75f, "classic 62–75%"),
    BREAD("bread", 65f..80f, "rustic 65–80%"),
    WHOLE_WHEAT("whole wheat", 72f..85f, "hearty 72–85%"),
}

data class SavedRecipe(
    val id: Long,
    val name: String,
    val totalWeight: Int,
    val hydrationPct: Int,
    val saltPct: Float,
    val yeastPct: Float,
    val flourType: FlourType = FlourType.FLOUR_00,
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
