package no.bylinnea.heve.model

enum class StepType(
    val label: String,
    val hasDuration: Boolean,
    val defaultMinutes: Int,
    val stepMinutes: Int,        // how much one +/- tap changes it
) {
    INGREDIENTS("add ingredients", hasDuration = false, defaultMinutes = 0, stepMinutes = 0),
    KNEAD("knead", hasDuration = true, defaultMinutes = 8, stepMinutes = 1),
    PROOF("proof", hasDuration = true, defaultMinutes = 45, stepMinutes = 5),
    FOLD("stretch & fold", hasDuration = false, defaultMinutes = 0, stepMinutes = 0),
    PRESHAPE("preshape", hasDuration = false, defaultMinutes = 0, stepMinutes = 0),
    SHAPE("shape", hasDuration = false, defaultMinutes = 0, stepMinutes = 0),
    BAKE("bake", hasDuration = true, defaultMinutes = 45, stepMinutes = 1),
}

data class JourneyStep(
    val id: Long,
    val type: StepType,
    val minutes: Int = type.defaultMinutes,
)

val sampleJourney: List<JourneyStep> = listOf(
    JourneyStep(1, StepType.INGREDIENTS),
    JourneyStep(2, StepType.KNEAD),
    JourneyStep(3, StepType.PROOF, 45),
    JourneyStep(4, StepType.FOLD),
    JourneyStep(5, StepType.PROOF, 45),
    JourneyStep(6, StepType.PRESHAPE),
    JourneyStep(7, StepType.PROOF, 30),
    JourneyStep(8, StepType.SHAPE),
    JourneyStep(9, StepType.PROOF, 30),
    JourneyStep(10, StepType.BAKE),
)