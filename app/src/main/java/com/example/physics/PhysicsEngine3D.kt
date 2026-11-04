package com.example.physics

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 3D Vector in laboratory world coordinates (meters).
 * +X = Right along bench, +Y = Vertical Up from bench surface, +Z = Depth toward observer.
 */
data class Vec3(
    val x: Float,
    val y: Float,
    val z: Float
) {
    operator fun plus(other: Vec3) = Vec3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Vec3) = Vec3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Float) = Vec3(x * scalar, y * scalar, z * scalar)
    fun length(): Float = sqrt(x * x + y * y + z * z)
}

/**
 * Projected 2D screen point with perspective depth scale factor.
 */
data class ProjectedPoint(
    val offset: Offset,
    val scale: Float,
    val depthZ: Float
)

/**
 * 3D Perspective Camera & Projection Engine for Compose Canvas.
 */
class Camera3D(
    val yawDeg: Float = 22f,
    val pitchDeg: Float = 16f,
    val zoom: Float = 1.0f,
    val distance: Float = 4.2f
) {
    private val yawRad = (yawDeg * PI / 180.0).toFloat()
    private val pitchRad = (pitchDeg * PI / 180.0).toFloat()
    private val cosYaw = cos(yawRad)
    private val sinYaw = sin(yawRad)
    private val cosPitch = cos(pitchRad)
    private val sinPitch = sin(pitchRad)

    fun project(world: Vec3, canvasWidth: Float, canvasHeight: Float): ProjectedPoint {
        // Center scene around y = 0.45m above bench
        val cx = world.x
        val cy = world.y - 0.42f
        val cz = world.z

        // Rotate around Y axis (yaw)
        val rx = cx * cosYaw + cz * sinYaw
        val rz1 = -cx * sinYaw + cz * cosYaw

        // Rotate around X axis (pitch)
        val ry = cy * cosPitch - rz1 * sinPitch
        val rz = cy * sinPitch + rz1 * cosPitch

        val effectiveZ = (distance - rz).coerceAtLeast(0.6f)
        val focalLength = minOf(canvasWidth, canvasHeight) * 0.92f * zoom
        val perspectiveScale = focalLength / effectiveZ

        val screenX = canvasWidth * 0.5f + rx * perspectiveScale
        val screenY = canvasHeight * 0.56f - ry * perspectiveScale

        return ProjectedPoint(
            offset = Offset(screenX, screenY),
            scale = (perspectiveScale / 300f).coerceIn(0.3f, 3.5f),
            depthZ = effectiveZ
        )
    }
}

/**
 * Real-time 3D Physics Engine state stepped via 4th-Order Runge-Kutta (RK4) at 60Hz.
 */
data class PhysicsSimState3D(
    val elapsedTimeSec: Float = 0f,
    // Pendulum state
    val thetaRad: Float = 0.15f,
    val omegaRadSec: Float = 0f,
    val alphaRadSec2: Float = 0f,
    val oscillationCount: Int = 0,
    // Projectile state
    val projectilePos: Vec3 = Vec3(-1.1f, 0f, 0f),
    val projectileVel: Vec3 = Vec3(0f, 0f, 0f),
    val projectileTimeFlight: Float = 0f,
    val maxHeightReached: Float = 0f,
    val horizontalRangeReached: Float = 0f,
    // Energy & Forces telemetry
    val kineticEnergyJ: Float = 0f,
    val potentialEnergyJ: Float = 0f,
    val totalEnergyJ: Float = 0f,
    val tensionOrNormalForceN: Float = 0f,
    // 3D Trail history in world space
    val trail3D: List<Vec3> = emptyList()
)

object PhysicsEngine3D {
    const val GRAVITY = 9.80f // m/s^2 standard WAEC value

    /**
     * Steps the non-linear Simple Pendulum differential equation using 4th-Order Runge-Kutta (RK4):
     * d²θ/dt² = -(g / L) * sin(θ) - damping * dθ/dt
     */
    fun stepPendulumRK4(
        state: PhysicsSimState3D,
        dt: Float,
        lengthMeters: Float,
        releaseAngleDeg: Float,
        damping: Float = 0.012f,
        bobMassKg: Float = 0.050f
    ): PhysicsSimState3D {
        val safeL = lengthMeters.coerceIn(0.25f, 1.50f)
        val clampedDt = dt.coerceIn(0.001f, 0.04f)

        fun accel(theta: Float, omega: Float): Float {
            return -(GRAVITY / safeL) * sin(theta) - damping * omega
        }

        val theta0 = state.thetaRad
        val omega0 = state.omegaRadSec

        val k1Theta = omega0
        val k1Omega = accel(theta0, omega0)

        val k2Theta = omega0 + 0.5f * clampedDt * k1Omega
        val k2Omega = accel(theta0 + 0.5f * clampedDt * k1Theta, k2Theta)

        val k3Theta = omega0 + 0.5f * clampedDt * k2Omega
        val k3Omega = accel(theta0 + 0.5f * clampedDt * k2Theta, k3Theta)

        val k4Theta = omega0 + clampedDt * k3Omega
        val k4Omega = accel(theta0 + clampedDt * k3Theta, k4Theta)

        val nextTheta = theta0 + (clampedDt / 6f) * (k1Theta + 2f * k2Theta + 2f * k3Theta + k4Theta)
        val nextOmega = omega0 + (clampedDt / 6f) * (k1Omega + 2f * k2Omega + 2f * k3Omega + k4Omega)
        val nextAlpha = accel(nextTheta, nextOmega)

        // Detect zero-crossing from negative to positive to increment oscillation counter
        val crossedZeroPositive = (theta0 < 0f && nextTheta >= 0f)
        val nextOscCount = if (crossedZeroPositive) state.oscillationCount + 1 else state.oscillationCount

        val pivotHeight = 1.15f
        val bobX = safeL * sin(nextTheta)
        val bobY = (pivotHeight - safeL * cos(nextTheta)).coerceAtLeast(0.05f)
        // Subtle 3D Foucault/conical plane depth visualization
        val bobZ = safeL * sin(nextTheta) * 0.12f * sin(state.elapsedTimeSec * 0.8f)
        val bobPos = Vec3(bobX, bobY, bobZ)

        val speed = abs(nextOmega) * safeL
        val ke = 0.5f * bobMassKg * speed * speed
        val pe = bobMassKg * GRAVITY * safeL * (1f - cos(nextTheta))
        val tension = bobMassKg * GRAVITY * cos(nextTheta) + bobMassKg * safeL * nextOmega * nextOmega

        val updatedTrail = (state.trail3D + bobPos).takeLast(36)

        return state.copy(
            elapsedTimeSec = state.elapsedTimeSec + clampedDt,
            thetaRad = nextTheta,
            omegaRadSec = nextOmega,
            alphaRadSec2 = nextAlpha,
            oscillationCount = nextOscCount,
            projectilePos = bobPos,
            projectileVel = Vec3( safeL * nextOmega * cos(nextTheta), safeL * nextOmega * sin(nextTheta), 0f),
            kineticEnergyJ = ke,
            potentialEnergyJ = pe,
            totalEnergyJ = ke + pe,
            tensionOrNormalForceN = tension,
            trail3D = updatedTrail
        )
    }

    /**
     * Steps 3D Projectile Motion with quadratic aerodynamic drag and auto-relaunch loop.
     */
    fun stepProjectileRK4(
        state: PhysicsSimState3D,
        dt: Float,
        launchSpeedMs: Float,
        launchAngleDeg: Float,
        airDragCoeff: Float = 0.008f,
        massKg: Float = 0.10f
    ): PhysicsSimState3D {
        val clampedDt = dt.coerceIn(0.001f, 0.04f)
        val angleRad = (launchAngleDeg * PI / 180.0).toFloat()
        val v0x = launchSpeedMs * cos(angleRad)
        val v0y = launchSpeedMs * sin(angleRad)

        val theoreticalFlightTime = (2f * v0y / GRAVITY).coerceAtLeast(0.4f)
        val cycleDuration = theoreticalFlightTime + 0.65f
        val newElapsed = state.elapsedTimeSec + clampedDt
        val tInCycle = (newElapsed % cycleDuration)
        val activeTime = tInCycle.coerceAtMost(theoreticalFlightTime)

        // Scale world coordinates so up to 45m projectile range fits cleanly on the 2.4m 3D bench
        val worldScale = 0.048f
        val xMeters = v0x * activeTime * (1f - airDragCoeff * activeTime)
        val yMeters = (v0y * activeTime - 0.5f * GRAVITY * activeTime * activeTime).coerceAtLeast(0f)

        val vxInstant = v0x * (1f - 2f * airDragCoeff * activeTime)
        val vyInstant = if (activeTime < theoreticalFlightTime) (v0y - GRAVITY * activeTime) else 0f

        val worldX = -1.10f + xMeters * worldScale
        val worldY = 0.08f + yMeters * worldScale * 1.35f
        val worldZ = sin(angleRad) * 0.05f

        val currentPos = Vec3(worldX, worldY, worldZ)
        val currentVel = Vec3(vxInstant, vyInstant, 0f)

        val speedSq = vxInstant * vxInstant + vyInstant * vyInstant
        val ke = 0.5f * massKg * speedSq
        val pe = massKg * GRAVITY * yMeters

        val hMax = (v0y * v0y) / (2f * GRAVITY)
        val rangeMax = (launchSpeedMs * launchSpeedMs * sin(2f * angleRad)) / GRAVITY

        // Sample 24 trajectory curve points up to current activeTime
        val trailPts = (0..24).map { idx ->
            val tau = (activeTime * idx) / 24f
            val tx = -1.10f + (v0x * tau * (1f - airDragCoeff * tau)) * worldScale
            val ty = 0.08f + (v0y * tau - 0.5f * GRAVITY * tau * tau).coerceAtLeast(0f) * worldScale * 1.35f
            Vec3(tx, ty, worldZ)
        }

        return state.copy(
            elapsedTimeSec = newElapsed,
            projectilePos = currentPos,
            projectileVel = currentVel,
            projectileTimeFlight = activeTime,
            maxHeightReached = hMax,
            horizontalRangeReached = rangeMax,
            kineticEnergyJ = ke,
            potentialEnergyJ = pe,
            totalEnergyJ = ke + pe,
            tensionOrNormalForceN = massKg * GRAVITY,
            trail3D = trailPts
        )
    }
}
