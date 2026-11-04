package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.ClassAssignment
import com.example.data.model.LabAttempt
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class LabRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun createAndGetUserAttempts_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = LabRepository(firestore, auth)
        val uniqueId = "att_${UUID.randomUUID().toString().replace("-", "")}"

        val attempt = LabAttempt(
            id = uniqueId,
            userId = uid,
            studentName = "Amina Bello",
            candidateNumber = "4250101088",
            experimentId = "mech_pendulum",
            experimentTitle = "Simple Pendulum Determination of g",
            category = "Mechanics",
            examBoard = "WAEC",
            score = 90L,
            maxScore = 100L,
            slopeCalculated = 4.02,
            errorPercentage = 1.5,
            aiFeedback = "Well plotted best-fit line."
        )

        val createRes = withTimeout(DEFAULT_TIMEOUT_MS) { repository.createLabAttempt(attempt) }
        assertTrue("Expected createLabAttempt to succeed: ${createRes.exceptionOrNull()}", createRes.isSuccess)

        val listRes = withTimeout(DEFAULT_TIMEOUT_MS) { repository.getUserAttempts(uid) }
        assertTrue(listRes.isSuccess)
        assertTrue(listRes.getOrThrow().any { it.id == uniqueId })
    }

    @Test
    fun observeUserAttempts_authenticatedOwner_emitsRealtimeUpdates() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = LabRepository(firestore, auth)
        val uniqueId = "att_${UUID.randomUUID().toString().replace("-", "")}"

        val attempt = LabAttempt(
            id = uniqueId,
            userId = uid,
            studentName = "Amina Bello",
            candidateNumber = "4250101088",
            experimentId = "opt_convex_lens",
            experimentTitle = "Focal Length of Convex Lens",
            category = "Light",
            examBoard = "NECO",
            score = 88L,
            maxScore = 100L,
            slopeCalculated = 0.066,
            errorPercentage = 2.1,
            aiFeedback = "Zero parallax observed."
        )
        withTimeout(DEFAULT_TIMEOUT_MS) { repository.createLabAttempt(attempt).getOrThrow() }

        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserAttempts(uid).first { list -> list.any { it.id == uniqueId } }
        }
        assertTrue(emitted.any { it.id == uniqueId })
    }

    @Test
    fun getAttemptById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = LabRepository(firestore, auth)
        val uniqueId = "att_${UUID.randomUUID().toString().replace("-", "")}"

        val attempt = LabAttempt(
            id = uniqueId,
            userId = aliceUid,
            studentName = "Amina Bello",
            candidateNumber = "4250101088",
            experimentId = "mech_pendulum",
            experimentTitle = "Simple Pendulum",
            category = "Mechanics",
            examBoard = "WAEC",
            score = 92L,
            maxScore = 100L,
            slopeCalculated = 4.01,
            errorPercentage = 1.1,
            aiFeedback = "Great work."
        )
        withTimeout(DEFAULT_TIMEOUT_MS) { aliceRepo.createLabAttempt(attempt).getOrThrow() }

        signInTestUser(BOB_EMAIL)
        val bobRepo = LabRepository(firestore, auth)
        val crossReadResult = withTimeout(DEFAULT_TIMEOUT_MS) { bobRepo.getAttemptById(uniqueId) }
        assertTrue(crossReadResult.isFailure)
        val fsEx = crossReadResult.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull(fsEx)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsEx?.code)
    }

    @Test
    fun observeUserAttempts_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = LabRepository(firestore, auth)

        var caught: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeUserAttempts("unauth_uid").first()
            }
        } catch (e: Throwable) {
            caught = e
        }
        assertNotNull("Expected exception when unauthenticated", caught)
        val firestoreException = caught?.unwrapFirestoreException()
        assertNotNull("Expected nested FirebaseFirestoreException, got $caught", firestoreException)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
    }

    @Test
    fun createAndCloseClassAssignment_authenticatedTeacher_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = LabRepository(firestore, auth)
        val uniqueId = "ass_${UUID.randomUUID().toString().replace("-", "")}"

        val assignment = ClassAssignment(
            id = uniqueId,
            userId = uid,
            teacherName = "Mr. Okafor",
            classLevel = "SS3",
            experimentId = "elec_potentiometer",
            experimentTitle = "Potentiometer EMF Comparison",
            examBoard = "WAEC",
            dueDate = "2026-12-01",
            targetAccuracyPercent = 5L,
            status = "active"
        )
        val createRes = withTimeout(DEFAULT_TIMEOUT_MS) { repository.createClassAssignment(assignment) }
        assertTrue(createRes.isSuccess)

        val closeRes = withTimeout(DEFAULT_TIMEOUT_MS) { repository.closeClassAssignment(uniqueId) }
        assertTrue(closeRes.isSuccess)
    }

    private companion object {
        const val ALICE_EMAIL = "alice@naijaphyslab.edu.ng"
        const val BOB_EMAIL = "bob@naijaphyslab.edu.ng"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
