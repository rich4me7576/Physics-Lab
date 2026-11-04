package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LabCatalogData
import com.example.data.model.PracticalExperimentSpec
import com.example.data.model.SimulationType
import com.example.physics.Camera3D
import com.example.physics.PhysicsEngine3D
import com.example.physics.PhysicsSimState3D
import com.example.physics.Vec3
import com.example.ui.theme.LabBrassGold
import com.example.ui.theme.LabCardEmerald
import com.example.ui.theme.LabDeepSlate
import com.example.ui.theme.LabErrorCoral
import com.example.ui.theme.LabLaserCyan
import com.example.ui.theme.LabPhosphorGreen
import com.example.ui.theme.LabThermalAmber
import com.example.viewmodel.LabViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WorkbenchScreen(
    viewModel: LabViewModel,
    isExpanded: Boolean,
    onNavigateToGraph: () -> Unit
) {
    val selectedExp by viewModel.selectedExperiment.collectAsStateWithLifecycle()
    val param1 by viewModel.param1.collectAsStateWithLifecycle()
    val param2 by viewModel.param2.collectAsStateWithLifecycle()
    val isCalibrated by viewModel.isCalibrated.collectAsStateWithLifecycle()
    val cameraYaw by viewModel.cameraYaw.collectAsStateWithLifecycle()
    val cameraPitch by viewModel.cameraPitch.collectAsStateWithLifecycle()
    val cameraZoom by viewModel.cameraZoom.collectAsStateWithLifecycle()
    val showVectors by viewModel.showVectors3D.collectAsStateWithLifecycle()
    val isRunning by viewModel.isRunningSimulation.collectAsStateWithLifecycle()
    val plottedPoints by viewModel.plottedPoints.collectAsStateWithLifecycle()

    // Real-time 60Hz RK4 3D Physics Engine state
    var simState by remember(selectedExp.id) {
        val initTheta = if (selectedExp.simType == SimulationType.PENDULUM_GRAVITY) {
            (param2 * PI / 180.0).toFloat()
        } else 0.15f
        mutableStateOf(PhysicsSimState3D(thetaRad = initTheta))
    }

    // Reset pendulum release angle when user adjusts param2 slider
    LaunchedEffect(param2, selectedExp.simType) {
        if (selectedExp.simType == SimulationType.PENDULUM_GRAVITY) {
            simState = simState.copy(
                thetaRad = (param2 * PI / 180.0).toFloat(),
                omegaRadSec = 0f,
                trail3D = emptyList()
            )
        }
    }

    LaunchedEffect(isRunning, selectedExp.simType, param1, param2) {
        var lastFrameMs = 0L
        while (isRunning) {
            withFrameMillis { frameMs ->
                val dt = if (lastFrameMs == 0L) 0.016f else ((frameMs - lastFrameMs) / 1000f).coerceIn(0.004f, 0.033f)
                lastFrameMs = frameMs
                simState = when (selectedExp.simType) {
                    SimulationType.PENDULUM_GRAVITY -> PhysicsEngine3D.stepPendulumRK4(
                        state = simState,
                        dt = dt,
                        lengthMeters = param1,
                        releaseAngleDeg = param2
                    )
                    SimulationType.PROJECTILE_MOTION -> PhysicsEngine3D.stepProjectileRK4(
                        state = simState,
                        dt = dt,
                        launchSpeedMs = param2,
                        launchAngleDeg = param1
                    )
                    else -> simState.copy(elapsedTimeSec = simState.elapsedTimeSec + dt)
                }
            }
        }
    }

    val (_, _, liveTelemetryText) = viewModel.computeCurrentMeasurement(param1, param2)

    if (isExpanded) {
        // Canonical Big-Screen / Tablet / Windows Desktop Split-Pane Laboratory Layout
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left Pane (58% width): Large High-Visibility 3D Viewport + Telemetry + Procedure
            Column(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ExperimentSelectorBar(selectedExp = selectedExp, onSelect = { viewModel.selectExperiment(it) })
                ExperimentHeaderCard(selectedExp = selectedExp, isCalibrated = isCalibrated)
                Viewport3DCanvasBox(
                    selectedExp = selectedExp,
                    param1 = param1,
                    param2 = param2,
                    cameraYaw = cameraYaw,
                    cameraPitch = cameraPitch,
                    cameraZoom = cameraZoom,
                    showVectors = showVectors,
                    isRunning = isRunning,
                    simState = simState,
                    liveTelemetryText = liveTelemetryText,
                    viewportHeight = 400.dp,
                    viewModel = viewModel
                )
                EnergyAndVectorTelemetryCard(simState = simState, selectedExp = selectedExp)
                ProcedureStepsCard(selectedExp = selectedExp)
            }

            // Right Pane (42% width): Apparatus Controls, Calibration & Live Observation Table
            Column(
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ApparatusControlsCard(
                    selectedExp = selectedExp,
                    param1 = param1,
                    param2 = param2,
                    isCalibrated = isCalibrated,
                    plottedCount = plottedPoints.size,
                    viewModel = viewModel
                )
                ObservationTableCard(
                    selectedExp = selectedExp,
                    plottedPoints = plottedPoints,
                    viewModel = viewModel,
                    onNavigateToGraph = onNavigateToGraph
                )
            }
        }
    } else {
        // Compact Handheld Single-Column Layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ExperimentSelectorBar(selectedExp = selectedExp, onSelect = { viewModel.selectExperiment(it) })
            ExperimentHeaderCard(selectedExp = selectedExp, isCalibrated = isCalibrated)
            Viewport3DCanvasBox(
                selectedExp = selectedExp,
                param1 = param1,
                param2 = param2,
                cameraYaw = cameraYaw,
                cameraPitch = cameraPitch,
                cameraZoom = cameraZoom,
                showVectors = showVectors,
                isRunning = isRunning,
                simState = simState,
                liveTelemetryText = liveTelemetryText,
                viewportHeight = 310.dp,
                viewModel = viewModel
            )
            EnergyAndVectorTelemetryCard(simState = simState, selectedExp = selectedExp)
            ApparatusControlsCard(
                selectedExp = selectedExp,
                param1 = param1,
                param2 = param2,
                isCalibrated = isCalibrated,
                plottedCount = plottedPoints.size,
                viewModel = viewModel
            )
            ObservationTableCard(
                selectedExp = selectedExp,
                plottedPoints = plottedPoints,
                viewModel = viewModel,
                onNavigateToGraph = onNavigateToGraph
            )
            ProcedureStepsCard(selectedExp = selectedExp)
        }
    }
}

@Composable
private fun ExperimentSelectorBar(
    selectedExp: PracticalExperimentSpec,
    onSelect: (PracticalExperimentSpec) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "WAEC / NECO / NERDC 3D Physics Apparatus (Drag 3D Viewport to Orbit)",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LabCatalogData.practicalExperiments.forEach { exp ->
                val isSelected = exp.id == selectedExp.id
                val shortName = when (exp.simType) {
                    SimulationType.PENDULUM_GRAVITY -> "3D Pendulum"
                    SimulationType.PROJECTILE_MOTION -> "3D Projectile"
                    SimulationType.CONVEX_LENS_OPTICS -> "3D Optics Lens"
                    SimulationType.POTENTIOMETER_OHMS_LAW -> "3D Potentiometer"
                    SimulationType.CALORIMETRY_HEAT -> "3D Calorimetry"
                    SimulationType.RESONANCE_TUBE_WAVES -> "3D Resonance"
                    SimulationType.ELECTROMAGNETIC_INDUCTION -> "3D Transformer"
                }
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelect(exp) },
                    label = {
                        Text(
                            text = "$shortName (${exp.examBoard})",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LabPhosphorGreen,
                        selectedLabelColor = Color(0xFF002912)
                    ),
                    modifier = Modifier.testTag("exp_chip_${exp.id}")
                )
            }
        }
    }
}

@Composable
private fun ExperimentHeaderCard(
    selectedExp: PracticalExperimentSpec,
    isCalibrated: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = LabBrassGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = selectedExp.yearSpecimen,
                        style = MaterialTheme.typography.labelSmall,
                        color = LabBrassGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Surface(
                    color = if (isCalibrated) LabPhosphorGreen.copy(alpha = 0.2f) else LabErrorCoral.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isCalibrated) "ZERO-ERROR CALIBRATED" else "UNCALIBRATED (+0.015 offset)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCalibrated) LabPhosphorGreen else LabErrorCoral,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = selectedExp.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Equation: ${selectedExp.theoryFormula}",
                style = MaterialTheme.typography.labelLarge,
                color = LabLaserCyan
            )
        }
    }
}

@Composable
private fun Viewport3DCanvasBox(
    selectedExp: PracticalExperimentSpec,
    param1: Float,
    param2: Float,
    cameraYaw: Float,
    cameraPitch: Float,
    cameraZoom: Float,
    showVectors: Boolean,
    isRunning: Boolean,
    simState: PhysicsSimState3D,
    liveTelemetryText: String,
    viewportHeight: Dp,
    viewModel: LabViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(viewportHeight)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF04120E), Color(0xFF0B271E))
                )
            )
            .border(1.5.dp, LabPhosphorGreen.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    viewModel.adjustCamera(
                        yawDelta = dragAmount.x * 0.25f,
                        zoomFactor = 1.0f,
                        pitchDelta = -dragAmount.y * 0.18f
                    )
                }
            }
            .testTag("physics_3d_viewport")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cam = Camera3D(yawDeg = cameraYaw, pitchDeg = cameraPitch, zoom = cameraZoom)

            // 1. Render 3D Perspective Laboratory Bench Grid (Y = 0 plane)
            val gridStepsX = 8
            val gridStepsZ = 6
            for (ix in -gridStepsX..gridStepsX) {
                val xPos = ix * 0.18f
                val pStart = cam.project(Vec3(xPos, 0f, -0.75f), w, h)
                val pEnd = cam.project(Vec3(xPos, 0f, 0.75f), w, h)
                drawLine(
                    color = if (ix == 0) LabPhosphorGreen.copy(alpha = 0.45f) else Color(0xFF194637),
                    start = pStart.offset,
                    end = pEnd.offset,
                    strokeWidth = if (ix == 0) 2.2f else 1.1f
                )
            }
            for (iz in -gridStepsZ..gridStepsZ) {
                val zPos = iz * 0.125f
                val pStart = cam.project(Vec3(-1.44f, 0f, zPos), w, h)
                val pEnd = cam.project(Vec3(1.44f, 0f, zPos), w, h)
                drawLine(
                    color = if (iz == 0) LabLaserCyan.copy(alpha = 0.40f) else Color(0xFF194637),
                    start = pStart.offset,
                    end = pEnd.offset,
                    strokeWidth = if (iz == 0) 2.2f else 1.1f
                )
            }

            // 2. Render 3D XYZ Coordinate Triad Gizmo at (-1.25, 0, 0.55)
            if (showVectors) {
                val origin3D = Vec3(-1.25f, 0.02f, 0.55f)
                val oProj = cam.project(origin3D, w, h).offset
                val xAxisProj = cam.project(origin3D + Vec3(0.22f, 0f, 0f), w, h).offset
                val yAxisProj = cam.project(origin3D + Vec3(0f, 0.22f, 0f), w, h).offset
                val zAxisProj = cam.project(origin3D + Vec3(0f, 0f, 0.22f), w, h).offset
                drawLine(color = LabThermalAmber, start = oProj, end = xAxisProj, strokeWidth = 3f)
                drawLine(color = LabPhosphorGreen, start = oProj, end = yAxisProj, strokeWidth = 3f)
                drawLine(color = LabLaserCyan, start = oProj, end = zAxisProj, strokeWidth = 3f)
            }

            // 3. Render Active 3D Physics Apparatus & Real-Time RK4 State
            when (selectedExp.simType) {
                SimulationType.PENDULUM_GRAVITY -> {
                    val standBaseCenter = Vec3(-0.32f, 0f, -0.15f)
                    val standTop = Vec3(-0.32f, 1.15f, -0.15f)
                    val pivot3D = Vec3(0f, 1.15f, 0f)
                    val bob3D = simState.projectilePos
                    val shadow3D = Vec3(bob3D.x, 0.0f, bob3D.z)

                    // 3D Retort Stand Base Plate
                    val b1 = cam.project(standBaseCenter + Vec3(-0.18f, 0f, -0.14f), w, h).offset
                    val b2 = cam.project(standBaseCenter + Vec3(0.18f, 0f, -0.14f), w, h).offset
                    val b3 = cam.project(standBaseCenter + Vec3(0.18f, 0f, 0.14f), w, h).offset
                    val b4 = cam.project(standBaseCenter + Vec3(-0.18f, 0f, 0.14f), w, h).offset
                    val basePath = Path().apply {
                        moveTo(b1.x, b1.y)
                        lineTo(b2.x, b2.y)
                        lineTo(b3.x, b3.y)
                        lineTo(b4.x, b4.y)
                        close()
                    }
                    drawPath(basePath, color = Color(0xFF455A64))
                    drawPath(basePath, color = Color(0xFF90A4AE), style = Stroke(width = 2f))

                    // Vertical Steel Rod & Bosshead Clamp Arm
                    val pStandBot = cam.project(standBaseCenter, w, h)
                    val pStandTop = cam.project(standTop, w, h)
                    val pPivot = cam.project(pivot3D, w, h)
                    drawLine(
                        color = Color(0xFFB0BEC5),
                        start = pStandBot.offset,
                        end = pStandTop.offset,
                        strokeWidth = 7f * pStandBot.scale
                    )
                    drawLine(
                        color = Color(0xFFCFD8DC),
                        start = pStandTop.offset,
                        end = pPivot.offset,
                        strokeWidth = 5.5f * pStandTop.scale
                    )

                    // 3D Vertical Metre Rule next to Pendulum
                    val ruleBot = cam.project(Vec3(-0.55f, 0f, 0f), w, h).offset
                    val ruleTop = cam.project(Vec3(-0.55f, 1.05f, 0f), w, h).offset
                    drawLine(color = LabBrassGold, start = ruleBot, end = ruleTop, strokeWidth = 8f)

                    // 3D Trajectory Ribbon Trail
                    if (simState.trail3D.size >= 2) {
                        val trailPath = Path()
                        simState.trail3D.forEachIndexed { idx, pt ->
                            val proj = cam.project(pt, w, h).offset
                            if (idx == 0) trailPath.moveTo(proj.x, proj.y)
                            else trailPath.lineTo(proj.x, proj.y)
                        }
                        drawPath(
                            path = trailPath,
                            color = LabPhosphorGreen.copy(alpha = 0.45f),
                            style = Stroke(width = 3f)
                        )
                    }

                    // Floor Shadow of Bob on Y=0 Bench
                    val pShadow = cam.project(shadow3D, w, h)
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.45f),
                        radius = 16f * pShadow.scale,
                        center = pShadow.offset
                    )

                    // Pendulum Inextensible String & 3D Brass Sphere Bob
                    val pBob = cam.project(bob3D, w, h)
                    drawLine(
                        color = Color.White,
                        start = pPivot.offset,
                        end = pBob.offset,
                        strokeWidth = 2.6f
                    )
                    val bobRadius = (20f * pBob.scale).coerceIn(10f, 36f)
                    drawCircle(
                        color = LabBrassGold,
                        radius = bobRadius,
                        center = pBob.offset
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.65f),
                        radius = bobRadius * 0.32f,
                        center = Offset(pBob.offset.x - bobRadius * 0.3f, pBob.offset.y - bobRadius * 0.3f)
                    )

                    // 3D Force & Velocity Vectors (Gravity mg, String Tension T, Velocity v)
                    if (showVectors) {
                        val gravEnd = cam.project(bob3D + Vec3(0f, -0.24f, 0f), w, h).offset
                        val velVec = simState.projectileVel * 0.16f
                        val velEnd = cam.project(bob3D + velVec, w, h).offset
                        // Gravity vector (Coral Red downward)
                        drawLine(color = LabErrorCoral, start = pBob.offset, end = gravEnd, strokeWidth = 3.5f)
                        // Velocity vector (Cyan tangent)
                        drawLine(color = LabLaserCyan, start = pBob.offset, end = velEnd, strokeWidth = 3.5f)
                    }
                }

                SimulationType.PROJECTILE_MOTION -> {
                    val launchOrigin = Vec3(-1.10f, 0.08f, 0f)
                    val angleRad = (param1 * PI / 180.0).toFloat()
                    val barrelEnd = launchOrigin + Vec3(0.24f * cos(angleRad), 0.24f * sin(angleRad), 0f)

                    // 1. 3D Ballistics Launcher Base & Angled Barrel
                    val pLaunchBase = cam.project(Vec3(-1.10f, 0f, 0f), w, h)
                    val pLaunchOrigin = cam.project(launchOrigin, w, h)
                    val pBarrelEnd = cam.project(barrelEnd, w, h)
                    drawCircle(
                        color = Color(0xFF546E7A),
                        radius = 18f * pLaunchBase.scale,
                        center = pLaunchBase.offset
                    )
                    drawLine(
                        color = LabBrassGold,
                        start = pLaunchOrigin.offset,
                        end = pBarrelEnd.offset,
                        strokeWidth = 11f * pLaunchOrigin.scale
                    )

                    // 2. Theoretical Full Parabolic Arc & Ground Shadow Track
                    val v0x = param2 * cos(angleRad)
                    val v0y = param2 * sin(angleRad)
                    val tTotal = (2f * v0y / PhysicsEngine3D.GRAVITY).coerceAtLeast(0.2f)
                    val worldScale = 0.048f

                    val ghostArc = Path()
                    val shadowArc = Path()
                    for (step in 0..32) {
                        val tau = tTotal * (step / 32f)
                        val gx = -1.10f + (v0x * tau) * worldScale
                        val gy = 0.08f + (v0y * tau - 0.5f * PhysicsEngine3D.GRAVITY * tau * tau).coerceAtLeast(0f) * worldScale * 1.35f
                        val pArc = cam.project(Vec3(gx, gy, 0f), w, h).offset
                        val pShad = cam.project(Vec3(gx, 0f, 0f), w, h).offset
                        if (step == 0) {
                            ghostArc.moveTo(pArc.x, pArc.y)
                            shadowArc.moveTo(pShad.x, pShad.y)
                        } else {
                            ghostArc.lineTo(pArc.x, pArc.y)
                            shadowArc.lineTo(pShad.x, pShad.y)
                        }
                    }
                    drawPath(shadowArc, color = Color.Black.copy(alpha = 0.35f), style = Stroke(width = 3f))
                    drawPath(ghostArc, color = LabLaserCyan.copy(alpha = 0.30f), style = Stroke(width = 2f))

                    // 3. Active 3D Projectile Trail & Strobe Nodes
                    if (simState.trail3D.size >= 2) {
                        val activePath = Path()
                        simState.trail3D.forEachIndexed { idx, pt ->
                            val proj = cam.project(pt, w, h).offset
                            if (idx == 0) activePath.moveTo(proj.x, proj.y)
                            else activePath.lineTo(proj.x, proj.y)
                            if (idx % 4 == 0) {
                                drawCircle(color = LabPhosphorGreen, radius = 3.5f, center = proj)
                            }
                        }
                        drawPath(activePath, color = LabPhosphorGreen, style = Stroke(width = 3.5f))
                    }

                    // 4. Target Impact Bullseye on 3D Bench
                    val impactX = -1.10f + (v0x * tTotal) * worldScale
                    val pTarget = cam.project(Vec3(impactX, 0.01f, 0f), w, h)
                    drawCircle(
                        color = LabErrorCoral.copy(alpha = 0.45f),
                        radius = 18f * pTarget.scale,
                        center = pTarget.offset
                    )
                    drawCircle(
                        color = LabBrassGold,
                        radius = 8f * pTarget.scale,
                        center = pTarget.offset
                    )

                    // 5. Projectile Sphere & 3D Velocity Decomposition Vectors (vx, vy)
                    val ball3D = simState.projectilePos
                    val pBall = cam.project(ball3D, w, h)
                    val pBallShadow = cam.project(Vec3(ball3D.x, 0f, ball3D.z), w, h)
                    drawCircle(
                        color = Color.Black.copy(alpha = 0.45f),
                        radius = 11f * pBallShadow.scale,
                        center = pBallShadow.offset
                    )
                    drawCircle(
                        color = LabThermalAmber,
                        radius = (14f * pBall.scale).coerceIn(8f, 26f),
                        center = pBall.offset
                    )

                    if (showVectors) {
                        val vxEnd = cam.project(ball3D + Vec3(simState.projectileVel.x * 0.018f, 0f, 0f), w, h).offset
                        val vyEnd = cam.project(ball3D + Vec3(0f, simState.projectileVel.y * 0.018f, 0f), w, h).offset
                        drawLine(color = LabLaserCyan, start = pBall.offset, end = vxEnd, strokeWidth = 3.2f)
                        drawLine(color = LabBrassGold, start = pBall.offset, end = vyEnd, strokeWidth = 3.2f)
                    }
                }

                SimulationType.CONVEX_LENS_OPTICS -> {
                    // 3D Twin-Rail Optical Bench along X-axis
                    val rail1Start = cam.project(Vec3(-1.25f, 0.08f, -0.08f), w, h).offset
                    val rail1End = cam.project(Vec3(1.25f, 0.08f, -0.08f), w, h).offset
                    val rail2Start = cam.project(Vec3(-1.25f, 0.08f, 0.08f), w, h).offset
                    val rail2End = cam.project(Vec3(1.25f, 0.08f, 0.08f), w, h).offset
                    drawLine(color = Color(0xFF90A4AE), start = rail1Start, end = rail1End, strokeWidth = 5f)
                    drawLine(color = Color(0xFF90A4AE), start = rail2Start, end = rail2End, strokeWidth = 5f)

                    val lensPos = Vec3(0f, 0.38f, 0f)
                    val objDistM = (param1 / 50f).coerceIn(0.25f, 1.15f)
                    val vCm = if (param1 > param2 + 0.5f) (param1 * param2) / (param1 - param2) else 55f
                    val imgDistM = (vCm / 50f).coerceIn(0.25f, 1.15f)

                    val objPos = Vec3(-objDistM, 0.38f, 0f)
                    val screenPos = Vec3(imgDistM, 0.38f, 0f)

                    val pObj = cam.project(objPos, w, h)
                    val pLens = cam.project(lensPos, w, h)
                    val pScreen = cam.project(screenPos, w, h)

                    // Object Box, Biconvex Lens & Image Screen in 3D space
                    drawCircle(color = LabBrassGold, radius = 18f * pObj.scale, center = pObj.offset)
                    drawCircle(color = LabLaserCyan.copy(alpha = 0.4f), radius = 34f * pLens.scale, center = pLens.offset)
                    drawCircle(color = LabLaserCyan, radius = 34f * pLens.scale, center = pLens.offset, style = Stroke(width = 3f))
                    drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 28f * pScreen.scale, center = pScreen.offset)

                    // 3D Principal Rays
                    val pLensTop = cam.project(lensPos + Vec3(0f, 0.16f, 0f), w, h).offset
                    val pObjTop = cam.project(objPos + Vec3(0f, 0.16f, 0f), w, h).offset
                    val pImgBot = cam.project(screenPos + Vec3(0f, -0.14f, 0f), w, h).offset
                    drawLine(color = LabPhosphorGreen, start = pObjTop, end = pLensTop, strokeWidth = 2.8f)
                    drawLine(color = LabPhosphorGreen, start = pLensTop, end = pImgBot, strokeWidth = 2.8f)
                    drawLine(color = LabBrassGold, start = pObjTop, end = pImgBot, strokeWidth = 2.8f)
                }

                SimulationType.POTENTIOMETER_OHMS_LAW -> {
                    // 3D Potentiometer Board & Dual Analog Meters
                    val boardLeft = cam.project(Vec3(-1.05f, 0.06f, 0.15f), w, h).offset
                    val boardRight = cam.project(Vec3(1.05f, 0.06f, 0.15f), w, h).offset
                    drawLine(color = Color(0xFF6D4C41), start = boardLeft, end = boardRight, strokeWidth = 18f)
                    drawLine(color = LabBrassGold, start = boardLeft, end = boardRight, strokeWidth = 3.5f)

                    val ammeterPos = cam.project(Vec3(-0.48f, 0.45f, -0.15f), w, h)
                    val voltmeterPos = cam.project(Vec3(0.48f, 0.45f, -0.15f), w, h)
                    drawCircle(color = Color(0xFF263238), radius = 38f * ammeterPos.scale, center = ammeterPos.offset)
                    drawCircle(color = LabPhosphorGreen, radius = 38f * ammeterPos.scale, center = ammeterPos.offset, style = Stroke(3f))
                    drawCircle(color = Color(0xFF263238), radius = 38f * voltmeterPos.scale, center = voltmeterPos.offset)
                    drawCircle(color = LabLaserCyan, radius = 38f * voltmeterPos.scale, center = voltmeterPos.offset, style = Stroke(3f))

                    val currentI = (1.5f / (param1 + param2)).coerceIn(0f, 1f)
                    val angle1 = -PI.toFloat() * 0.75f + currentI * PI.toFloat() * 0.6f
                    drawLine(
                        color = LabPhosphorGreen,
                        start = ammeterPos.offset,
                        end = Offset(ammeterPos.offset.x + 28f * cos(angle1), ammeterPos.offset.y + 28f * sin(angle1)),
                        strokeWidth = 3.5f
                    )
                    val voltV = (currentI * param1).coerceIn(0f, 1.5f)
                    val angle2 = -PI.toFloat() * 0.75f + (voltV / 1.5f) * PI.toFloat() * 0.6f
                    drawLine(
                        color = LabLaserCyan,
                        start = voltmeterPos.offset,
                        end = Offset(voltmeterPos.offset.x + 28f * cos(angle2), voltmeterPos.offset.y + 28f * sin(angle2)),
                        strokeWidth = 3.5f
                    )
                }

                SimulationType.CALORIMETRY_HEAT -> {
                    val calBot = cam.project(Vec3(0f, 0.08f, 0f), w, h)
                    val calTop = cam.project(Vec3(0f, 0.55f, 0f), w, h)
                    val thermTop = cam.project(Vec3(0f, 0.98f, 0f), w, h)
                    drawLine(
                        color = LabThermalAmber,
                        start = calBot.offset,
                        end = calTop.offset,
                        strokeWidth = 64f * calBot.scale
                    )
                    drawLine(
                        color = Color.White,
                        start = calBot.offset,
                        end = thermTop.offset,
                        strokeWidth = 8f * calBot.scale
                    )
                    val mercuryFrac = (param1 / 360f).coerceIn(0.15f, 0.95f)
                    val mercTop = cam.project(Vec3(0f, 0.15f + mercuryFrac * 0.72f, 0f), w, h)
                    drawLine(
                        color = LabErrorCoral,
                        start = calBot.offset,
                        end = mercTop.offset,
                        strokeWidth = 5f * calBot.scale
                    )
                }

                SimulationType.RESONANCE_TUBE_WAVES -> {
                    val tubeBot = cam.project(Vec3(0f, 0.05f, 0f), w, h)
                    val tubeTop = cam.project(Vec3(0f, 0.92f, 0f), w, h)
                    drawLine(
                        color = LabLaserCyan.copy(alpha = 0.35f),
                        start = tubeBot.offset,
                        end = tubeTop.offset,
                        strokeWidth = 38f * tubeBot.scale
                    )
                    val wavePath = Path()
                    for (i in 0..24) {
                        val frac = i / 24f
                        val yWorld = 0.25f + frac * 0.65f
                        val xWorld = 0.12f * sin(frac * PI.toFloat() * 0.5f) * sin(simState.elapsedTimeSec * 8f)
                        val pt = cam.project(Vec3(xWorld, yWorld, 0f), w, h).offset
                        if (i == 0) wavePath.moveTo(pt.x, pt.y) else wavePath.lineTo(pt.x, pt.y)
                    }
                    drawPath(wavePath, color = LabPhosphorGreen, style = Stroke(width = 3.5f))
                }

                SimulationType.ELECTROMAGNETIC_INDUCTION -> {
                    val leftCoil = cam.project(Vec3(-0.38f, 0.42f, 0f), w, h)
                    val rightCoil = cam.project(Vec3(0.38f, 0.42f, 0f), w, h)
                    drawLine(
                        color = Color(0xFF78909C),
                        start = leftCoil.offset,
                        end = rightCoil.offset,
                        strokeWidth = 28f * leftCoil.scale
                    )
                    drawCircle(color = LabThermalAmber, radius = 26f * leftCoil.scale, center = leftCoil.offset)
                    drawCircle(color = LabPhosphorGreen, radius = 26f * rightCoil.scale, center = rightCoil.offset)
                }
            }
        }

        // Top-Left 3D Camera & Vector Overlay Controls
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xD9071A15))
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.adjustCamera(-12f, 1.0f) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.RotateLeft, contentDescription = "Orbit Left", tint = Color.White)
            }
            IconButton(
                onClick = { viewModel.adjustCamera(12f, 1.0f) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.RotateRight, contentDescription = "Orbit Right", tint = Color.White)
            }
            IconButton(
                onClick = { viewModel.adjustCamera(0f, 1.14f) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White)
            }
            IconButton(
                onClick = { viewModel.adjustCamera(0f, 0.88f) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.White)
            }
            IconButton(
                onClick = { viewModel.toggleVectors3D() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.Timeline,
                    contentDescription = "Toggle 3D Force Vectors",
                    tint = if (showVectors) LabBrassGold else Color.Gray
                )
            }
            IconButton(
                onClick = { viewModel.toggleSimulationRunning() },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause 3D Physics",
                    tint = LabPhosphorGreen
                )
            }
        }

        // Top-Right Camera Angle Readout Pill
        Surface(
            color = Color(0xCC071A15),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
        ) {
            Text(
                text = "3D Cam: Yaw ${cameraYaw.toInt()}° | Pitch ${cameraPitch.toInt()}° | ${"%.1f".format(cameraZoom)}x",
                style = MaterialTheme.typography.labelSmall,
                color = LabLaserCyan,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Bottom Digital Instrument Readout Overlay
        Surface(
            color = Color(0xE6061510),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = liveTelemetryText,
                style = MaterialTheme.typography.labelLarge,
                color = LabPhosphorGreen,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun EnergyAndVectorTelemetryCard(
    simState: PhysicsSimState3D,
    selectedExp: PracticalExperimentSpec
) {
    if (selectedExp.simType != SimulationType.PENDULUM_GRAVITY &&
        selectedExp.simType != SimulationType.PROJECTILE_MOTION
    ) return

    Card(
        colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "3D RK4 Physics Engine Telemetry (60Hz)",
                    style = MaterialTheme.typography.labelLarge,
                    color = LabBrassGold
                )
                Text(
                    text = if (selectedExp.simType == SimulationType.PENDULUM_GRAVITY) {
                        "Oscillations: ${simState.oscillationCount} | Tension T = ${"%.2f".format(simState.tensionOrNormalForceN)} N"
                    } else {
                        "Flight t = ${"%.2f".format(simState.projectileTimeFlight)} s | H_max = ${"%.2f".format(simState.maxHeightReached)} m"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = LabPhosphorGreen
                )
            }

            val totalE = simState.totalEnergyJ.coerceAtLeast(0.0001f)
            val keFrac = (simState.kineticEnergyJ / totalE).coerceIn(0f, 1f)
            val peFrac = (simState.potentialEnergyJ / totalE).coerceIn(0f, 1f)

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("KE", style = MaterialTheme.typography.labelSmall, color = LabLaserCyan, modifier = Modifier.width(28.dp))
                LinearProgressIndicator(
                    progress = { keFrac },
                    color = LabLaserCyan,
                    trackColor = LabDeepSlate,
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
                Text("PE", style = MaterialTheme.typography.labelSmall, color = LabBrassGold, modifier = Modifier.width(28.dp))
                LinearProgressIndicator(
                    progress = { peFrac },
                    color = LabBrassGold,
                    trackColor = LabDeepSlate,
                    modifier = Modifier
                        .weight(1f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

@Composable
private fun ApparatusControlsCard(
    selectedExp: PracticalExperimentSpec,
    param1: Float,
    param2: Float,
    isCalibrated: Boolean,
    plottedCount: Int,
    viewModel: LabViewModel
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedExp.parameter1Label}: ${"%.2f".format(param1)} ${selectedExp.parameter1Unit}",
                    style = MaterialTheme.typography.titleMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = {
                            val step = (selectedExp.parameter1Max - selectedExp.parameter1Min) / 20f
                            viewModel.updateParam1(param1 - step)
                        },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                    }
                    OutlinedButton(
                        onClick = {
                            val step = (selectedExp.parameter1Max - selectedExp.parameter1Min) / 20f
                            viewModel.updateParam1(param1 + step)
                        },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                    }
                }
            }
            Slider(
                value = param1,
                onValueChange = { viewModel.updateParam1(it) },
                valueRange = selectedExp.parameter1Min..selectedExp.parameter1Max,
                colors = SliderDefaults.colors(
                    thumbColor = LabPhosphorGreen,
                    activeTrackColor = LabPhosphorGreen
                ),
                modifier = Modifier.testTag("slider_param1")
            )

            Text(
                text = "${selectedExp.parameter2Label}: ${"%.2f".format(param2)} ${selectedExp.parameter2Unit}",
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = param2,
                onValueChange = { viewModel.updateParam2(it) },
                valueRange = selectedExp.parameter2Min..selectedExp.parameter2Max,
                colors = SliderDefaults.colors(
                    thumbColor = LabBrassGold,
                    activeTrackColor = LabBrassGold
                ),
                modifier = Modifier.testTag("slider_param2")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.calibrateEquipment() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("calibrate_zero_button")
                ) {
                    Icon(
                        imageVector = if (isCalibrated) Icons.Default.CheckCircle else Icons.Default.CenterFocusStrong,
                        contentDescription = "Calibrate",
                        tint = if (isCalibrated) LabPhosphorGreen else LabBrassGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isCalibrated) "Calibrated" else "Zero-Calibrate")
                }

                Button(
                    onClick = { viewModel.recordCurrentReading() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LabPhosphorGreen,
                        contentColor = Color(0xFF002912)
                    ),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(48.dp)
                        .testTag("record_reading_button")
                ) {
                    Text(
                        text = "Record Reading (#${plottedCount + 1})",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ObservationTableCard(
    selectedExp: PracticalExperimentSpec,
    plottedPoints: List<com.example.data.model.PlottedDataPoint>,
    viewModel: LabViewModel,
    onNavigateToGraph: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Observation Table (${plottedPoints.size} readings)",
                    style = MaterialTheme.typography.titleMedium,
                    color = LabBrassGold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.seedInitialPointsForExperiment() },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Auto-Fill 6 Readings", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("6 Readings", style = MaterialTheme.typography.labelSmall)
                    }
                    Button(
                        onClick = onNavigateToGraph,
                        colors = ButtonDefaults.buttonColors(containerColor = LabLaserCyan, contentColor = Color(0xFF002538)),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("open_graph_sheet_button")
                    ) {
                        Text("Plot Graph →", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LabDeepSlate, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("S/N", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(0.5f), color = LabBrassGold)
                Text(selectedExp.xLabel, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.2f), color = LabBrassGold)
                Text(selectedExp.yLabel, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.2f), color = LabBrassGold)
                Text("±Tol", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(0.8f), color = LabBrassGold)
            }

            plottedPoints.forEach { pt ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("${pt.index}", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(0.5f))
                    Text("%.3f".format(pt.xVal), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.2f))
                    Text("%.3f".format(pt.yVal), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.2f), color = LabPhosphorGreen)
                    Text("±%.3f".format(pt.uncertainty), style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(0.8f))
                }
            }
        }
    }
}

@Composable
private fun ProcedureStepsCard(selectedExp: PracticalExperimentSpec) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LabCardEmerald.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "WAEC / NECO Laboratory Procedure",
                style = MaterialTheme.typography.titleMedium,
                color = LabLaserCyan
            )
            selectedExp.procedureSteps.forEach { step ->
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
