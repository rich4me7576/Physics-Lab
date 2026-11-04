package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ClassAssignment
import com.example.data.model.CurriculumDomainProgress
import com.example.data.model.ExperimentCompletionSummary
import com.example.data.model.LabAttempt
import com.example.data.model.LabCatalogData
import com.example.data.model.PlottedDataPoint
import com.example.data.model.PracticalExperimentSpec
import com.example.data.model.SimulationType
import com.example.data.repository.LabRepository
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

sealed interface AiTutorState {
    data object Idle : AiTutorState
    data object Loading : AiTutorState
    data class Streaming(val text: String) : AiTutorState
    data class Success(val text: String) : AiTutorState
    data class Error(val message: String) : AiTutorState
}

data class GraphRegressionResult(
    val slope: Double = 0.0,
    val yIntercept: Double = 0.0,
    val xIntercept: Double = 0.0,
    val rSquared: Double = 0.0,
    val slopeUncertainty: Double = 0.0,
    val errorPercent: Double = 0.0,
    val deducedConstantText: String = ""
)

class LabViewModel(
    private val repository: LabRepository,
    private val currentUserId: String,
    val studentDisplayName: String
) : ViewModel() {

    private val aiModel by lazy {
        Firebase.ai.generativeModel("gemini-3.8-flash")
    }

    val userAttempts: StateFlow<UiState<List<LabAttempt>>> = repository.observeUserAttempts(currentUserId)
        .map<List<LabAttempt>, UiState<List<LabAttempt>>> { list ->
            UiState.Success(list.sortedByDescending { it.createdAt?.seconds ?: 0L })
        }
        .catch { error ->
            Log.w(TAG, "Error observing lab attempts", error)
            emit(UiState.Error(error.message ?: "Failed to load saved lab attempts"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    val teacherAssignments: StateFlow<UiState<List<ClassAssignment>>> = repository.observeTeacherAssignments(currentUserId)
        .map<List<ClassAssignment>, UiState<List<ClassAssignment>>> { list ->
            UiState.Success(list.sortedByDescending { it.createdAt?.seconds ?: 0L })
        }
        .catch { error ->
            Log.w(TAG, "Error observing assignments", error)
            emit(UiState.Error(error.message ?: "Failed to load class assignments"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    private val _selectedExperiment = MutableStateFlow(LabCatalogData.practicalExperiments.first())
    val selectedExperiment: StateFlow<PracticalExperimentSpec> = _selectedExperiment.asStateFlow()

    // 3D Simulation Workbench parameters & state
    private val _param1 = MutableStateFlow(LabCatalogData.practicalExperiments.first().parameter1Default)
    val param1: StateFlow<Float> = _param1.asStateFlow()

    private val _param2 = MutableStateFlow(LabCatalogData.practicalExperiments.first().parameter2Default)
    val param2: StateFlow<Float> = _param2.asStateFlow()

    private val _isCalibrated = MutableStateFlow(false)
    val isCalibrated: StateFlow<Boolean> = _isCalibrated.asStateFlow()

    private val _zeroErrorOffset = MutableStateFlow(0.015)
    val zeroErrorOffset: StateFlow<Double> = _zeroErrorOffset.asStateFlow()

    private val _cameraYaw = MutableStateFlow(22f)
    val cameraYaw: StateFlow<Float> = _cameraYaw.asStateFlow()

    private val _cameraPitch = MutableStateFlow(16f)
    val cameraPitch: StateFlow<Float> = _cameraPitch.asStateFlow()

    private val _cameraZoom = MutableStateFlow(1.0f)
    val cameraZoom: StateFlow<Float> = _cameraZoom.asStateFlow()

    private val _showVectors3D = MutableStateFlow(true)
    val showVectors3D: StateFlow<Boolean> = _showVectors3D.asStateFlow()

    private val _forceWideLabMode = MutableStateFlow(false)
    val forceWideLabMode: StateFlow<Boolean> = _forceWideLabMode.asStateFlow()

    private val _isRunningSimulation = MutableStateFlow(true)
    val isRunningSimulation: StateFlow<Boolean> = _isRunningSimulation.asStateFlow()

    // Recorded observations & graphing data
    private val _plottedPoints = MutableStateFlow<List<PlottedDataPoint>>(emptyList())
    val plottedPoints: StateFlow<List<PlottedDataPoint>> = _plottedPoints.asStateFlow()

    private val _isLogScale = MutableStateFlow(false)
    val isLogScale: StateFlow<Boolean> = _isLogScale.asStateFlow()

    private val _showBestFitLine = MutableStateFlow(true)
    val showBestFitLine: StateFlow<Boolean> = _showBestFitLine.asStateFlow()

    private val _showSlopeTriangle = MutableStateFlow(true)
    val showSlopeTriangle: StateFlow<Boolean> = _showSlopeTriangle.asStateFlow()

    // WAEC / NECO Examination Mode state
    private val _candidateNumber = MutableStateFlow(
        "42501" + (abs(currentUserId.hashCode()) % 90000 + 10000).toString()
    )
    val candidateNumber: StateFlow<String> = _candidateNumber.asStateFlow()

    private val _examBoard = MutableStateFlow("WAEC")
    val examBoard: StateFlow<String> = _examBoard.asStateFlow()

    private val _examTimerRunning = MutableStateFlow(false)
    val examTimerRunning: StateFlow<Boolean> = _examTimerRunning.asStateFlow()

    private val _examSecondsRemaining = MutableStateFlow(9900) // 2h 45m = 165 mins = 9900s
    val examSecondsRemaining: StateFlow<Int> = _examSecondsRemaining.asStateFlow()

    private val _studentPrecautions = MutableStateFlow(
        "1. Avoided parallax error when reading the metre rule/scale by viewing at eye level.\n" +
            "2. Repeated timings/readings and took the mean to reduce random timing error."
    )
    val studentPrecautions: StateFlow<String> = _studentPrecautions.asStateFlow()

    private val _studentShortAnswer = MutableStateFlow(
        "The bob is displaced through a small angle (θ < 10°) so that sin(θ) ≈ θ in radians and the motion remains simple harmonic."
    )
    val studentShortAnswer: StateFlow<String> = _studentShortAnswer.asStateFlow()

    private val _statusBanner = MutableStateFlow<String?>(null)
    val statusBanner: StateFlow<String?> = _statusBanner.asStateFlow()

    // AI Tutor state
    private val _aiTutorState = MutableStateFlow<AiTutorState>(AiTutorState.Idle)
    val aiTutorState: StateFlow<AiTutorState> = _aiTutorState.asStateFlow()

    private val _simpleEnglishMode = MutableStateFlow(false)
    val simpleEnglishMode: StateFlow<Boolean> = _simpleEnglishMode.asStateFlow()

    // Dashboard: Session-tracked completed experiment summaries & mastered curriculum objectives
    private val _sessionCompletedSummaries = MutableStateFlow<Map<String, ExperimentCompletionSummary>>(
        seedInitialExperimentSummaries()
    )
    val sessionCompletedSummaries: StateFlow<Map<String, ExperimentCompletionSummary>> =
        _sessionCompletedSummaries.asStateFlow()

    private val _completedObjectiveKeys = MutableStateFlow<Set<String>>(
        setOf(
            "curr_mech:0", "curr_mech:1", "curr_mech:2",
            "curr_energy:0", "curr_energy:1",
            "curr_matter:0", "curr_matter:1",
            "curr_heat:0", "curr_heat:1",
            "curr_waves:0", "curr_waves:1",
            "curr_light:0", "curr_light:1",
            "curr_elec:0", "curr_elec:1",
            "curr_mag:0", "curr_mag:1",
            "curr_modern:0",
            "curr_comm:0"
        )
    )
    val completedObjectiveKeys: StateFlow<Set<String>> = _completedObjectiveKeys.asStateFlow()

    private var timerJob: Job? = null

    init {
        seedInitialPointsForExperiment(LabCatalogData.practicalExperiments.first())
    }

    fun selectExperiment(spec: PracticalExperimentSpec) {
        _selectedExperiment.value = spec
        _param1.value = spec.parameter1Default
        _param2.value = spec.parameter2Default
        _examBoard.value = spec.examBoard
        _studentPrecautions.value = spec.standardPrecautions.take(2).mapIndexed { i, s -> "${i + 1}. $s" }.joinToString("\n")
        seedInitialPointsForExperiment(spec)
    }

    fun updateParam1(value: Float) {
        val spec = _selectedExperiment.value
        _param1.value = value.coerceIn(spec.parameter1Min, spec.parameter1Max)
    }

    fun updateParam2(value: Float) {
        val spec = _selectedExperiment.value
        _param2.value = value.coerceIn(spec.parameter2Min, spec.parameter2Max)
    }

    fun calibrateEquipment() {
        _isCalibrated.value = true
        _zeroErrorOffset.value = 0.0
        _statusBanner.value = "Zero-error calibrated! Instrument datum aligned (+2 accuracy bonus marks)."
    }

    fun adjustCamera(yawDelta: Float, zoomFactor: Float, pitchDelta: Float = 0f) {
        _cameraYaw.value = (_cameraYaw.value + yawDelta).coerceIn(-80f, 80f)
        _cameraPitch.value = (_cameraPitch.value + pitchDelta).coerceIn(-5f, 55f)
        _cameraZoom.value = (_cameraZoom.value * zoomFactor).coerceIn(0.65f, 2.15f)
    }

    fun resetCamera3D() {
        _cameraYaw.value = 22f
        _cameraPitch.value = 16f
        _cameraZoom.value = 1.0f
    }

    fun toggleVectors3D() {
        _showVectors3D.value = !_showVectors3D.value
    }

    fun toggleWideLabMode() {
        _forceWideLabMode.value = !_forceWideLabMode.value
    }

    fun toggleSimulationRunning() {
        _isRunningSimulation.value = !_isRunningSimulation.value
    }

    fun toggleLogScale() {
        _isLogScale.value = !_isLogScale.value
    }

    fun toggleBestFitLine() {
        _showBestFitLine.value = !_showBestFitLine.value
    }

    fun toggleSlopeTriangle() {
        _showSlopeTriangle.value = !_showSlopeTriangle.value
    }

    fun setExamBoard(board: String) {
        _examBoard.value = board
    }

    fun updateCandidateNumber(num: String) {
        _candidateNumber.value = num.take(15)
    }

    fun updateStudentPrecautions(text: String) {
        _studentPrecautions.value = text
    }

    fun updateStudentShortAnswer(text: String) {
        _studentShortAnswer.value = text
    }

    fun toggleSimpleEnglishMode() {
        _simpleEnglishMode.value = !_simpleEnglishMode.value
    }

    fun clearStatusBanner() {
        _statusBanner.value = null
    }

    fun toggleExamTimer() {
        if (_examTimerRunning.value) {
            _examTimerRunning.value = false
            timerJob?.cancel()
        } else {
            _examTimerRunning.value = true
            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                while (_examSecondsRemaining.value > 0 && _examTimerRunning.value) {
                    delay(1000L)
                    _examSecondsRemaining.value = (_examSecondsRemaining.value - 1).coerceAtLeast(0)
                }
                _examTimerRunning.value = false
            }
        }
    }

    fun resetExamTimer() {
        _examTimerRunning.value = false
        timerJob?.cancel()
        _examSecondsRemaining.value = 9900
    }

    /**
     * Computes instantaneous real-world physics output for the current apparatus settings.
     */
    fun computeCurrentMeasurement(p1: Float = _param1.value, p2: Float = _param2.value): Triple<Double, Double, String> {
        val spec = _selectedExperiment.value
        val zeroBias = if (_isCalibrated.value) 0.0 else _zeroErrorOffset.value

        return when (spec.simType) {
            SimulationType.PENDULUM_GRAVITY -> {
                val lengthM = p1.toDouble() + zeroBias
                val thetaRad = p2.toDouble() * PI / 180.0
                val g = 9.80
                val periodT = 2.0 * PI * sqrt(lengthM / g) * (1.0 + (thetaRad * thetaRad) / 16.0)
                val tSquared = periodT * periodT
                val time20 = periodT * 20.0
                Triple(
                    lengthM,
                    tSquared,
                    "L = %.3f m | t(20 osc) = %.2f s | T = %.3f s | T² = %.3f s²".format(lengthM, time20, periodT, tSquared)
                )
            }
            SimulationType.PROJECTILE_MOTION -> {
                val angleDeg = (p1.toDouble() + zeroBias * 20.0).coerceIn(10.0, 80.0)
                val speedU = p2.toDouble()
                val g = 9.80
                val twoThetaRad = (2.0 * angleDeg) * PI / 180.0
                val thetaRad = angleDeg * PI / 180.0
                val sin2Theta = kotlin.math.sin(twoThetaRad)
                val sinTheta = kotlin.math.sin(thetaRad)
                val rangeR = ((speedU * speedU) / g) * sin2Theta
                val hMax = ((speedU * speedU) * sinTheta * sinTheta) / (2.0 * g)
                val tFlight = (2.0 * speedU * sinTheta) / g
                Triple(
                    sin2Theta,
                    rangeR,
                    "θ = %.1f° | u = %.1f m/s | sin(2θ) = %.3f | Range R = %.2f m | H_max = %.2f m | T = %.2f s".format(
                        angleDeg, speedU, sin2Theta, rangeR, hMax, tFlight
                    )
                )
            }
            SimulationType.CONVEX_LENS_OPTICS -> {
                val uCm = p1.toDouble() + zeroBias * 10.0
                val fCm = p2.toDouble()
                val vCm = if (uCm > fCm + 0.5) (uCm * fCm) / (uCm - fCm) else 999.0
                val invU = 1.0 / uCm
                val invV = 1.0 / vCm
                val mag = vCm / uCm
                Triple(
                    invU,
                    invV,
                    "u = %.1f cm | v = %.1f cm | 1/u = %.4f cm⁻¹ | 1/v = %.4f cm⁻¹ | M = %.2fx".format(uCm, vCm, invU, invV, mag)
                )
            }
            SimulationType.POTENTIOMETER_OHMS_LAW -> {
                val rBox = p1.toDouble()
                val rInternal = p2.toDouble()
                val emf = 1.50
                val currentI = emf / (rBox + rInternal)
                val terminalV = currentI * rBox
                val invI = 1.0 / currentI
                Triple(
                    invI,
                    rBox,
                    "R = %.1f Ω | I = %.3f A | V = %.2f V | 1/I = %.3f A⁻¹".format(rBox, currentI, terminalV, invI)
                )
            }
            SimulationType.CALORIMETRY_HEAT -> {
                val timeSec = p1.toDouble()
                val powerW = p2.toDouble()
                val waterMassKg = 0.100
                val cw = 4200.0
                val calorimeterCap = 32.0 // 80g Cu * 400 J/kgK
                val coolingFactor = 0.96
                val deltaTheta = (powerW * timeSec * coolingFactor) / (waterMassKg * cw + calorimeterCap)
                Triple(
                    timeSec,
                    deltaTheta,
                    "t = %.0f s | P = %.1f W | θ₀ = 27.0°C | θ = %.2f°C | Δθ = %.2f°C".format(timeSec, powerW, 27.0 + deltaTheta, deltaTheta)
                )
            }
            SimulationType.RESONANCE_TUBE_WAVES -> {
                val freqHz = p1.toDouble()
                val speedSoundCmS = 34300.0
                val endCorrectionCm = 1.02
                val resonantL1 = (speedSoundCmS / (4.0 * freqHz)) - endCorrectionCm
                val invF = 1000.0 / freqHz // in 10^-3 s
                Triple(
                    invF,
                    resonantL1,
                    "f = %.1f Hz | 1/f = %.3f ×10⁻³ s | Resonant L₁ = %.2f cm".format(freqHz, invF, resonantL1)
                )
            }
            SimulationType.ELECTROMAGNETIC_INDUCTION -> {
                val secondaryTurns = p1.toDouble()
                val primaryVoltage = p2.toDouble()
                val primaryTurns = 400.0
                val couplingEfficiency = 0.95
                val secondaryV = couplingEfficiency * primaryVoltage * (secondaryTurns / primaryTurns)
                Triple(
                    secondaryTurns,
                    secondaryV,
                    "N_p = 400 | V_p = %.1f V | N_s = %.0f turns | Induced V_s = %.2f V".format(primaryVoltage, secondaryTurns, secondaryV)
                )
            }
        }
    }

    fun recordCurrentReading() {
        val (xVal, yVal, _) = computeCurrentMeasurement()
        // Add tiny realistic experimental jitter (±0.6%)
        val jitter = 1.0 + (Random.nextDouble(-0.006, 0.006))
        val noisyY = yVal * jitter
        val currentList = _plottedPoints.value.toMutableList()
        val newPoint = PlottedDataPoint(
            index = currentList.size + 1,
            rawInput = _param1.value.toDouble(),
            rawOutput = noisyY,
            xVal = xVal,
            yVal = noisyY,
            uncertainty = abs(noisyY * 0.015)
        )
        currentList.add(newPoint)
        _plottedPoints.value = currentList.sortedBy { it.xVal }.mapIndexed { idx, pt -> pt.copy(index = idx + 1) }
        _statusBanner.value = "Recorded reading #${_plottedPoints.value.size} to Practical Observation Table."
    }

    fun addManualPoint(xVal: Double, yVal: Double) {
        val currentList = _plottedPoints.value.toMutableList()
        currentList.add(
            PlottedDataPoint(
                index = currentList.size + 1,
                rawInput = xVal,
                rawOutput = yVal,
                xVal = xVal,
                yVal = yVal,
                uncertainty = abs(yVal * 0.02)
            )
        )
        _plottedPoints.value = currentList.sortedBy { it.xVal }.mapIndexed { idx, pt -> pt.copy(index = idx + 1) }
    }

    fun clearReadings() {
        _plottedPoints.value = emptyList()
    }

    fun seedInitialPointsForExperiment(spec: PracticalExperimentSpec = _selectedExperiment.value) {
        val steps = 6
        val minP1 = spec.parameter1Min
        val maxP1 = if (spec.simType == SimulationType.PROJECTILE_MOTION) 45f else spec.parameter1Max
        val stepSize = (maxP1 - minP1) / (steps + 1)
        val generated = (1..steps).map { i ->
            val p1Val = minP1 + stepSize * i
            val (x, y, _) = computeCurrentMeasurement(p1Val, spec.parameter2Default)
            val microError = 1.0 + ((i % 3 - 1) * 0.004)
            val yFinal = y * microError
            PlottedDataPoint(
                index = i,
                rawInput = p1Val.toDouble(),
                rawOutput = yFinal,
                xVal = x,
                yVal = yFinal,
                uncertainty = abs(yFinal * 0.015)
            )
        }.sortedBy { it.xVal }.mapIndexed { idx, pt -> pt.copy(index = idx + 1) }
        _plottedPoints.value = generated
    }

    fun computeRegression(): GraphRegressionResult {
        val pts = _plottedPoints.value
        if (pts.size < 2) return GraphRegressionResult()

        val transformed = pts.map { pt ->
            val x = if (_isLogScale.value && pt.xVal > 0) log10(pt.xVal) else pt.xVal
            val y = if (_isLogScale.value && pt.yVal > 0) log10(pt.yVal) else pt.yVal
            x to y
        }

        val n = transformed.size.toDouble()
        val sumX = transformed.sumOf { it.first }
        val sumY = transformed.sumOf { it.second }
        val sumXY = transformed.sumOf { it.first * it.second }
        val sumX2 = transformed.sumOf { it.first * it.first }
        val sumY2 = transformed.sumOf { it.second * it.second }

        val denom = (n * sumX2 - sumX * sumX)
        if (abs(denom) < 1e-12) return GraphRegressionResult()

        val slope = (n * sumXY - sumX * sumY) / denom
        val intercept = (sumY - slope * sumX) / n
        val xInt = if (abs(slope) > 1e-9) -intercept / slope else 0.0

        val ssTot = sumY2 - (sumY * sumY) / n
        val ssRes = transformed.sumOf { (x, y) ->
            val pred = slope * x + intercept
            (y - pred).pow(2)
        }
        val r2 = if (ssTot > 1e-12) (1.0 - ssRes / ssTot).coerceIn(0.0, 1.0) else 1.0
        val slopeStdErr = if (n > 2 && abs(denom) > 1e-9) {
            sqrt((ssRes / (n - 2.0)) / (sumX2 - (sumX * sumX) / n))
        } else 0.0

        val spec = _selectedExperiment.value
        val target = if (spec.simType == SimulationType.RESONANCE_TUBE_WAVES) {
            8.575
        } else {
            spec.targetSlope
        }
        val errorPct = if (abs(target) > 1e-9) {
            (abs(slope - target) / abs(target) * 100.0).coerceIn(0.0, 99.9)
        } else 0.0

        val deduction = when (spec.simType) {
            SimulationType.PENDULUM_GRAVITY -> {
                val gDeduced = if (abs(slope) > 0.1) (4.0 * PI * PI) / slope else 0.0
                "Deduced g = 4π²/slope = %.2f m/s² (Standard: 9.80 m/s²)".format(gDeduced)
            }
            SimulationType.PROJECTILE_MOTION -> {
                val speedU = _param2.value.toDouble()
                val gDeduced = if (abs(slope) > 0.1) (speedU * speedU) / slope else 0.0
                "Deduced R_max = %.2f m | g = u²/slope = %.2f m/s²".format(slope, gDeduced)
            }
            SimulationType.CONVEX_LENS_OPTICS -> {
                val fFromIntercept = if (abs(intercept) > 1e-4) 1.0 / intercept else 0.0
                "Deduced Focal Length f = 1/Intercept = %.2f cm".format(fFromIntercept)
            }
            SimulationType.POTENTIOMETER_OHMS_LAW -> {
                "Deduced EMF E = %.2f V | Internal Resistance r = %.2f Ω".format(slope, abs(intercept))
            }
            SimulationType.CALORIMETRY_HEAT -> {
                val cwDeduced = if (abs(slope) > 1e-5) ((_param2.value / slope) - 32.0) / 0.100 else 0.0
                "Deduced Specific Heat c_w ≈ %.0f J/(kg·K) (Standard: 4200 J/kg·K)".format(cwDeduced)
            }
            SimulationType.RESONANCE_TUBE_WAVES -> {
                val vSound = slope * 4.0
                "Deduced Speed of Sound v = %.1f m/s | End Correction c = %.2f cm".format(vSound * 10.0, abs(intercept))
            }
            SimulationType.ELECTROMAGNETIC_INDUCTION -> {
                "Flux Coupling Efficiency η = %.1f%%".format((slope / (spec.parameter2Default / 400.0)) * 100.0)
            }
        }

        return GraphRegressionResult(
            slope = slope,
            yIntercept = intercept,
            xIntercept = xInt,
            rSquared = r2,
            slopeUncertainty = slopeStdErr,
            errorPercent = errorPct,
            deducedConstantText = deduction
        )
    }

    fun calculateAutomatedExamScore(): Pair<Long, String> {
        val pts = _plottedPoints.value
        val reg = computeRegression()

        // 1. Table of Observations (up to 40 pts)
        val tableScore = (pts.size.coerceAtMost(6) * 6) + (if (_isCalibrated.value) 4 else 2)
        // 2. Graph Plotting & Best Fit Accuracy (up to 35 pts)
        val accuracyScore = when {
            pts.size < 3 -> 10
            reg.errorPercent <= 2.5 -> 35
            reg.errorPercent <= 5.0 -> 31
            reg.errorPercent <= 10.0 -> 25
            else -> 18
        }
        // 3. Precautions & Short Answer (up to 25 pts)
        val precautionScore = if (_studentPrecautions.value.length >= 40) 15 else 8
        val shortAnsScore = if (_studentShortAnswer.value.length >= 30) 10 else 5

        val total = (tableScore + accuracyScore + precautionScore + shortAnsScore).toLong().coerceIn(0L, 100L)
        val summary = "WAEC/NECO Automated Rubric Breakdown: Table (${tableScore}/40) + Graph & Slope (${accuracyScore}/35, Error=${"%.2f".format(reg.errorPercent)}%) + Precautions & Theory (${precautionScore + shortAnsScore}/25) = $total/100. ${reg.deducedConstantText}"
        return total to summary
    }

    fun submitExamAttemptToCloud() {
        val spec = _selectedExperiment.value
        val reg = computeRegression()
        val (score, feedbackSummary) = calculateAutomatedExamScore()

        // Also update the session dashboard summary immediately
        recordCurrentExperimentToDashboard(showSnackbar = false)

        viewModelScope.launch {
            val attempt = LabAttempt(
                userId = currentUserId,
                studentName = studentDisplayName.ifBlank { "WAEC Candidate" },
                candidateNumber = _candidateNumber.value,
                experimentId = spec.id,
                experimentTitle = spec.title,
                category = spec.category,
                examBoard = _examBoard.value,
                score = score,
                maxScore = 100L,
                slopeCalculated = reg.slope,
                errorPercentage = reg.errorPercent,
                aiFeedback = feedbackSummary.take(1900)
            )
            val result = repository.createLabAttempt(attempt)
            if (result.isSuccess) {
                _statusBanner.value = "Practical Booklet graded ($score/100) & synced to Cloud Dashboard!"
            } else {
                _statusBanner.value = "Saved to local Dashboard ($score/100); Cloud sync: ${result.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    fun recordCurrentExperimentToDashboard(showSnackbar: Boolean = true) {
        val spec = _selectedExperiment.value
        val reg = computeRegression()
        val (score, _) = calculateAutomatedExamScore()
        val existing = _sessionCompletedSummaries.value[spec.id]
        val newAttempts = (existing?.attemptsCount ?: 0) + 1
        val bestScore = maxOf(score, existing?.bestScore ?: 0L)
        val lowestErr = if (existing != null && existing.isCompleted) {
            minOf(reg.errorPercent, existing.lowestErrorPercent)
        } else {
            reg.errorPercent
        }
        val grade = computeWaecGrade(bestScore)
        val updatedSummary = ExperimentCompletionSummary(
            experimentId = spec.id,
            title = spec.title,
            category = spec.category,
            examBoard = spec.examBoard,
            bestScore = bestScore,
            attemptsCount = newAttempts,
            bestSlope = reg.slope,
            targetSlope = spec.targetSlope,
            slopeUnit = spec.slopeUnit,
            lowestErrorPercent = lowestErr,
            deducedConstantSummary = reg.deducedConstantText.ifBlank { "Slope = %.4f %s".format(reg.slope, spec.slopeUnit) },
            lastCompletedLabel = "Just now (Active Session)",
            isCompleted = true,
            masteryGrade = grade,
            linkedCurriculumDomain = mapCategoryToCurriculumDomain(spec.category)
        )
        _sessionCompletedSummaries.value = _sessionCompletedSummaries.value.toMutableMap().apply {
            put(spec.id, updatedSummary)
        }

        // Automatically mark all objectives in the linked curriculum domain as mastered
        val currId = mapCategoryToCurriculumId(spec.category)
        val currItem = LabCatalogData.curriculumMatrix.find { it.id == currId }
        if (currItem != null) {
            val updatedKeys = _completedObjectiveKeys.value.toMutableSet()
            currItem.learningObjectives.indices.forEach { idx ->
                updatedKeys.add("$currId:$idx")
            }
            _completedObjectiveKeys.value = updatedKeys
        }

        if (showSnackbar) {
            _statusBanner.value = "Logged '${spec.title.substringBefore(":")}' ($score/100 • $grade) to Progress Dashboard!"
        }
    }

    fun toggleCurriculumObjective(curriculumId: String, objectiveIndex: Int) {
        val key = "$curriculumId:$objectiveIndex"
        val current = _completedObjectiveKeys.value.toMutableSet()
        if (current.contains(key)) {
            current.remove(key)
        } else {
            current.add(key)
        }
        _completedObjectiveKeys.value = current
    }

    fun buildExperimentSummaries(
        cloudAttempts: List<LabAttempt>,
        sessionMap: Map<String, ExperimentCompletionSummary>
    ): List<ExperimentCompletionSummary> {
        return LabCatalogData.practicalExperiments.map { spec ->
            val cloudForSpec = cloudAttempts.filter { it.experimentId == spec.id }
            val sessionEntry = sessionMap[spec.id]

            if (cloudForSpec.isNotEmpty()) {
                val bestCloud = cloudForSpec.maxByOrNull { it.score }!!
                val combinedAttempts = cloudForSpec.size + (sessionEntry?.attemptsCount ?: 0)
                val bestScore = maxOf(bestCloud.score, sessionEntry?.bestScore ?: 0L)
                val lowestError = minOf(
                    bestCloud.errorPercentage,
                    sessionEntry?.lowestErrorPercent ?: bestCloud.errorPercentage
                )
                ExperimentCompletionSummary(
                    experimentId = spec.id,
                    title = spec.title,
                    category = spec.category,
                    examBoard = bestCloud.examBoard.ifBlank { spec.examBoard },
                    bestScore = bestScore,
                    attemptsCount = combinedAttempts,
                    bestSlope = bestCloud.slopeCalculated,
                    targetSlope = spec.targetSlope,
                    slopeUnit = spec.slopeUnit,
                    lowestErrorPercent = lowestError,
                    deducedConstantSummary = sessionEntry?.deducedConstantSummary
                        ?: bestCloud.aiFeedback.substringAfterLast(". ").ifBlank { "Slope = %.4f %s".format(bestCloud.slopeCalculated, spec.slopeUnit) },
                    lastCompletedLabel = "Synced to Cloud Firestore",
                    isCompleted = true,
                    masteryGrade = computeWaecGrade(bestScore),
                    linkedCurriculumDomain = mapCategoryToCurriculumDomain(spec.category)
                )
            } else if (sessionEntry != null) {
                sessionEntry
            } else {
                ExperimentCompletionSummary(
                    experimentId = spec.id,
                    title = spec.title,
                    category = spec.category,
                    examBoard = spec.examBoard,
                    bestScore = 0L,
                    attemptsCount = 0,
                    bestSlope = 0.0,
                    targetSlope = spec.targetSlope,
                    slopeUnit = spec.slopeUnit,
                    lowestErrorPercent = 0.0,
                    deducedConstantSummary = "Awaiting practical execution (${spec.theoryFormula.substringBefore("⇒").trim()})",
                    lastCompletedLabel = "Not started yet",
                    isCompleted = false,
                    masteryGrade = "Pending Practical",
                    linkedCurriculumDomain = mapCategoryToCurriculumDomain(spec.category)
                )
            }
        }
    }

    fun buildCurriculumProgress(
        summaries: List<ExperimentCompletionSummary>,
        checkedKeys: Set<String>
    ): List<CurriculumDomainProgress> {
        val completedIds = summaries.filter { it.isCompleted }.map { it.experimentId }.toSet()
        return LabCatalogData.curriculumMatrix.map { item ->
            val totalObj = item.learningObjectives.size.coerceAtLeast(1)
            val completedObj = item.learningObjectives.indices.count { idx ->
                checkedKeys.contains("${item.id}:$idx")
            }
            val linkedExpId = mapCurriculumIdToExperimentId(item.id)
            val totalPrac = item.practicalActivities.size.coerceAtLeast(1)
            val isLinkedExpDone = linkedExpId != null && completedIds.contains(linkedExpId)
            val completedPrac = when {
                isLinkedExpDone && completedObj == totalObj -> totalPrac
                isLinkedExpDone -> (totalPrac - 1).coerceAtLeast(1)
                completedObj > 0 -> (completedObj.toFloat() / totalObj * totalPrac).toInt().coerceAtLeast(1)
                else -> 0
            }
            val mastery = ((completedObj.toFloat() / totalObj) * 0.65f +
                (completedPrac.toFloat() / totalPrac) * 0.35f) * 100f

            CurriculumDomainProgress(
                curriculumItem = item,
                completedObjectivesCount = completedObj,
                totalObjectivesCount = totalObj,
                completedPracticalsCount = completedPrac,
                totalPracticalsCount = totalPrac,
                masteryPercent = mastery.coerceIn(0f, 100f),
                linkedExperimentId = linkedExpId
            )
        }
    }

    private fun computeWaecGrade(score: Long): String = when {
        score >= 85L -> "A1 Distinction"
        score >= 75L -> "B2 Very Good"
        score >= 65L -> "B3 Good"
        score >= 55L -> "C4 Credit"
        score >= 50L -> "C6 Credit"
        score > 0L -> "D7 Pass"
        else -> "Pending"
    }

    private fun mapCategoryToCurriculumDomain(category: String): String = when (category) {
        "Mechanics" -> "1. Mechanics & 2. Energy"
        "Optics" -> "6. Light & Optics"
        "Electricity" -> "7. Electricity"
        "Heat" -> "4. Heat & Thermodynamics"
        "Waves" -> "5. Wave Motion & Sound"
        "Magnetism" -> "8. Magnetism & Electromagnetism"
        else -> "NERDC Core Physics"
    }

    private fun mapCategoryToCurriculumId(category: String): String = when (category) {
        "Mechanics" -> "curr_mech"
        "Optics" -> "curr_light"
        "Electricity" -> "curr_elec"
        "Heat" -> "curr_heat"
        "Waves" -> "curr_waves"
        "Magnetism" -> "curr_mag"
        else -> "curr_mech"
    }

    private fun mapCurriculumIdToExperimentId(curriculumId: String): String? = when (curriculumId) {
        "curr_mech" -> "mech_pendulum"
        "curr_energy" -> "mech_projectile"
        "curr_matter" -> "mech_pendulum"
        "curr_heat" -> "heat_calorimetry"
        "curr_waves" -> "wave_resonance"
        "curr_light" -> "opt_convex_lens"
        "curr_elec" -> "elec_potentiometer"
        "curr_mag" -> "mag_induction"
        "curr_modern" -> "elec_potentiometer"
        "curr_comm" -> "wave_resonance"
        else -> null
    }

    private fun seedInitialExperimentSummaries(): Map<String, ExperimentCompletionSummary> {
        return mapOf(
            "mech_pendulum" to ExperimentCompletionSummary(
                experimentId = "mech_pendulum",
                title = "WAEC/NECO Q1: Simple Pendulum — Determination of Acceleration due to Gravity (g)",
                category = "Mechanics",
                examBoard = "WAEC",
                bestScore = 94L,
                attemptsCount = 3,
                bestSlope = 4.028,
                targetSlope = 4.028,
                slopeUnit = "s²/m",
                lowestErrorPercent = 0.42,
                deducedConstantSummary = "Deduced g = 4π²/slope = 9.81 m/s² (Standard: 9.80 m/s²)",
                lastCompletedLabel = "Completed • Verified 6 Readings",
                isCompleted = true,
                masteryGrade = "A1 Distinction",
                linkedCurriculumDomain = "1. Mechanics"
            ),
            "mech_projectile" to ExperimentCompletionSummary(
                experimentId = "mech_projectile",
                title = "WAEC/NECO Mechanics: 3D Projectile Motion — Range, Max Height & Trajectory",
                category = "Mechanics",
                examBoard = "WAEC",
                bestScore = 91L,
                attemptsCount = 2,
                bestSlope = 22.91,
                targetSlope = 22.96,
                slopeUnit = "m",
                lowestErrorPercent = 0.68,
                deducedConstantSummary = "Deduced R_max = 22.91 m | g = u²/slope = 9.82 m/s²",
                lastCompletedLabel = "Completed • 3D RK4 Trajectory Verified",
                isCompleted = true,
                masteryGrade = "A1 Distinction",
                linkedCurriculumDomain = "1. Mechanics & 2. Energy"
            ),
            "opt_convex_lens" to ExperimentCompletionSummary(
                experimentId = "opt_convex_lens",
                title = "WAEC/NECO Q2: Optics — Focal Length of a Convex Lens by Ray Tracing & 1/v vs 1/u",
                category = "Optics",
                examBoard = "WAEC",
                bestScore = 88L,
                attemptsCount = 2,
                bestSlope = -0.992,
                targetSlope = -1.000,
                slopeUnit = "dimensionless",
                lowestErrorPercent = 0.80,
                deducedConstantSummary = "Deduced Focal Length f = 1/Intercept = 15.04 cm (Target: 15.0 cm)",
                lastCompletedLabel = "Completed • No-Parallax Verified",
                isCompleted = true,
                masteryGrade = "A1 Distinction",
                linkedCurriculumDomain = "6. Light & Optics"
            ),
            "elec_potentiometer" to ExperimentCompletionSummary(
                experimentId = "elec_potentiometer",
                title = "WAEC/NECO Q3: Electricity — EMF and Internal Resistance of a Dry Cell",
                category = "Electricity",
                examBoard = "WAEC",
                bestScore = 84L,
                attemptsCount = 2,
                bestSlope = 1.485,
                targetSlope = 1.500,
                slopeUnit = "V (Ω·A)",
                lowestErrorPercent = 1.00,
                deducedConstantSummary = "Deduced EMF E = 1.49 V | Internal Resistance r = 1.18 Ω",
                lastCompletedLabel = "Completed • Null Balance Verified",
                isCompleted = true,
                masteryGrade = "B2 Very Good",
                linkedCurriculumDomain = "7. Electricity"
            ),
            "heat_calorimetry" to ExperimentCompletionSummary(
                experimentId = "heat_calorimetry",
                title = "NECO/NERDC Q4: Heat — Specific Heat Capacity by Electrical Calorimetry",
                category = "Heat",
                examBoard = "NECO",
                bestScore = 79L,
                attemptsCount = 1,
                bestSlope = 0.0532,
                targetSlope = 0.0540,
                slopeUnit = "°C/s",
                lowestErrorPercent = 1.48,
                deducedConstantSummary = "Deduced Specific Heat c_w ≈ 4188 J/(kg·K) (Standard: 4200 J/kg·K)",
                lastCompletedLabel = "Completed • Lagged Calorimeter",
                isCompleted = true,
                masteryGrade = "B2 Very Good",
                linkedCurriculumDomain = "4. Heat & Thermodynamics"
            )
        )
    }

    fun createTeacherAssignment(
        classLevel: String,
        experimentSpec: PracticalExperimentSpec,
        examBoard: String,
        dueDate: String,
        targetAccuracy: Long
    ) {
        viewModelScope.launch {
            val assignment = ClassAssignment(
                userId = currentUserId,
                teacherName = studentDisplayName.ifBlank { "Physics Teacher" },
                classLevel = classLevel,
                experimentId = experimentSpec.id,
                experimentTitle = experimentSpec.title,
                examBoard = examBoard,
                dueDate = dueDate.ifBlank { "2026-11-30" },
                targetAccuracyPercent = targetAccuracy.coerceIn(1L, 25L),
                status = "active"
            )
            val res = repository.createClassAssignment(assignment)
            if (res.isSuccess) {
                _statusBanner.value = "Assigned '${experimentSpec.title}' to $classLevel ($examBoard)!"
            } else {
                _statusBanner.value = "Failed to create assignment: ${res.exceptionOrNull()?.localizedMessage}"
            }
        }
    }

    fun closeAssignment(assignmentId: String) {
        viewModelScope.launch {
            repository.closeClassAssignment(assignmentId)
        }
    }

    fun askAiPhysicsTutor(userQuestion: String) {
        val spec = _selectedExperiment.value
        val reg = computeRegression()
        val languageStyle = if (_simpleEnglishMode.value) {
            "Use VERY SIMPLE, clear everyday English with relatable Nigerian classroom analogies suitable for an SS1–SS3 student in a rural school."
        } else {
            "Use precise WAEC/NECO Chief Examiner scientific Physics terminology aligned with the NERDC curriculum."
        }

        val prompt = """
            You are the AI Physics Lab Tutor & WAEC/NECO Chief Examiner for NaijaPhysLab 3D.
            Language Mode: $languageStyle
            Current Experiment: ${spec.title} (${spec.examBoard} - ${spec.yearSpecimen})
            Formula: ${spec.theoryFormula}
            Student's Recorded Readings: ${_plottedPoints.value.size} points
            Calculated Slope: ${"%.4f".format(reg.slope)} (${spec.slopeUnit}) | Target Slope: ${spec.targetSlope}
            Experimental Error: ${"%.2f".format(reg.errorPercent)}%
            Deduction: ${reg.deducedConstantText}
            Student's Stated Precautions: ${_studentPrecautions.value}
            
            Student Request / Question:
            $userQuestion
            
            Provide a well-structured, encouraging, and scientifically accurate response covering:
            1. Direct answer or diagnosis of the student's experimental readings and slope.
            2. WAEC/NECO Chief Examiner marking tips (significant figures, units, precautions in past tense).
            3. Next practical step.
        """.trimIndent()

        viewModelScope.launch {
            _aiTutorState.value = AiTutorState.Loading
            val accumulatedText = StringBuilder()
            try {
                withTimeout(60_000L) {
                    aiModel.generateContentStream(prompt).collect { chunk ->
                        val chunkText = chunk.text.orEmpty()
                        accumulatedText.append(chunkText)
                        _aiTutorState.value = AiTutorState.Streaming(accumulatedText.toString())
                    }
                }
                if (accumulatedText.isNotBlank()) {
                    _aiTutorState.value = AiTutorState.Success(accumulatedText.toString())
                } else {
                    _aiTutorState.value = AiTutorState.Error("Model returned an empty response.")
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "AI Tutor streaming timed out", e)
                _aiTutorState.value = AiTutorState.Error("AI Tutor timed out after 60 seconds. Please try again.")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "AI Tutor generation failed", e)
                _aiTutorState.value = AiTutorState.Error(e.localizedMessage ?: "AI Tutor request failed.")
            }
        }
    }

    private companion object {
        const val TAG = "LabViewModel"
        const val STOP_TIMEOUT_MILLIS = 5000L
    }
}
