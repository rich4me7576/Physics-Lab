package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class LabAttempt(
    @DocumentId val id: String = "",
    val userId: String = "",
    val studentName: String = "",
    val candidateNumber: String = "",
    val experimentId: String = "",
    val experimentTitle: String = "",
    val category: String = "",
    val examBoard: String = "WAEC",
    val score: Long = 0L,
    val maxScore: Long = 100L,
    val slopeCalculated: Double = 0.0,
    val errorPercentage: Double = 0.0,
    val aiFeedback: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class ClassAssignment(
    @DocumentId val id: String = "",
    val userId: String = "",
    val teacherName: String = "",
    val classLevel: String = "SS3",
    val experimentId: String = "",
    val experimentTitle: String = "",
    val examBoard: String = "WAEC",
    val dueDate: String = "",
    val targetAccuracyPercent: Long = 5L,
    val status: String = "active",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

data class CurriculumItem(
    val id: String,
    val domain: String,
    val subTopics: List<String>,
    val nerdcCode: String,
    val waecSyllabusRef: String,
    val necoSyllabusRef: String,
    val learningObjectives: List<String>,
    val practicalActivities: List<String>,
    val requiredEquipment: List<String>,
    val simulationRequirements: String,
    val assessmentMethods: List<String>
)

data class VirtualEquipment(
    val id: String,
    val name: String,
    val category: String,
    val dimensionsCm: String,
    val massGrams: Double,
    val measurementAccuracy: String,
    val zeroErrorRange: String,
    val physicsBehaviour: String,
    val calibrationProcedure: String
)

enum class SimulationType {
    PENDULUM_GRAVITY,
    PROJECTILE_MOTION,
    CONVEX_LENS_OPTICS,
    POTENTIOMETER_OHMS_LAW,
    CALORIMETRY_HEAT,
    RESONANCE_TUBE_WAVES,
    ELECTROMAGNETIC_INDUCTION
}

data class PracticalExperimentSpec(
    val id: String,
    val title: String,
    val category: String,
    val examBoard: String,
    val yearSpecimen: String,
    val simType: SimulationType,
    val aim: String,
    val apparatus: List<String>,
    val theoryFormula: String,
    val targetSlope: Double,
    val slopeUnit: String,
    val xLabel: String,
    val yLabel: String,
    val procedureSteps: List<String>,
    val standardPrecautions: List<String>,
    val shortAnswerQuestions: List<String>,
    val parameter1Label: String,
    val parameter1Min: Float,
    val parameter1Max: Float,
    val parameter1Default: Float,
    val parameter1Unit: String,
    val parameter2Label: String,
    val parameter2Min: Float,
    val parameter2Max: Float,
    val parameter2Default: Float,
    val parameter2Unit: String
)

data class PlottedDataPoint(
    val index: Int,
    val rawInput: Double,
    val rawOutput: Double,
    val xVal: Double,
    val yVal: Double,
    val uncertainty: Double
)

data class BlueprintSection(
    val partNumber: Int,
    val title: String,
    val subtitle: String,
    val keyHighlights: List<String>,
    val detailedSpecification: String
)

data class PhysicsMission(
    val id: String,
    val title: String,
    val badgeTitle: String,
    val xpReward: Int,
    val description: String,
    val requiredExperimentId: String,
    val maxAllowedErrorPercent: Double
)

data class ExperimentCompletionSummary(
    val experimentId: String,
    val title: String,
    val category: String,
    val examBoard: String,
    val bestScore: Long,
    val attemptsCount: Int,
    val bestSlope: Double,
    val targetSlope: Double,
    val slopeUnit: String,
    val lowestErrorPercent: Double,
    val deducedConstantSummary: String,
    val lastCompletedLabel: String,
    val isCompleted: Boolean,
    val masteryGrade: String,
    val linkedCurriculumDomain: String
)

data class CurriculumDomainProgress(
    val curriculumItem: CurriculumItem,
    val completedObjectivesCount: Int,
    val totalObjectivesCount: Int,
    val completedPracticalsCount: Int,
    val totalPracticalsCount: Int,
    val masteryPercent: Float,
    val linkedExperimentId: String?
)

