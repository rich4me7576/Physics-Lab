package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.ClassAssignment
import com.example.data.model.LabAttempt
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

class LabRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserAttempts(userId: String): Flow<List<LabAttempt>> = flow {
        val path = "lab_attempts"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        LabAttempt::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun getUserAttempts(userId: String): Result<List<LabAttempt>> {
        val path = "lab_attempts"
        return try {
            val snapshot = db.collection(path)
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val list = snapshot.toObjects(
                LabAttempt::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(list)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.LIST, path)
            Result.failure(e)
        }
    }

    suspend fun getAttemptById(attemptId: String): Result<LabAttempt?> {
        val path = "lab_attempts/$attemptId"
        return try {
            val doc = db.collection("lab_attempts").document(attemptId).get().await()
            val item = doc.toObject(
                LabAttempt::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(item)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path)
            Result.failure(e)
        }
    }

    suspend fun createLabAttempt(attempt: LabAttempt): Result<String> {
        val uid = requireUserId()
        val collectionRef = db.collection("lab_attempts")
        val docRef = if (attempt.id.isNotBlank()) {
            collectionRef.document(attempt.id)
        } else {
            collectionRef.document()
        }
        val payload = mapOf(
            "userId" to uid,
            "studentName" to attempt.studentName.ifBlank { "WAEC Candidate" },
            "candidateNumber" to attempt.candidateNumber.ifBlank { "4250101001" },
            "experimentId" to attempt.experimentId,
            "experimentTitle" to attempt.experimentTitle,
            "category" to attempt.category,
            "examBoard" to attempt.examBoard,
            "score" to attempt.score,
            "maxScore" to attempt.maxScore,
            "slopeCalculated" to attempt.slopeCalculated,
            "errorPercentage" to attempt.errorPercentage,
            "aiFeedback" to attempt.aiFeedback,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun updateLabAttemptScore(
        attemptId: String,
        score: Long,
        slopeCalculated: Double,
        errorPercentage: Double,
        aiFeedback: String
    ): Result<Unit> {
        requireUserId()
        val docRef = db.collection("lab_attempts").document(attemptId)
        val updates = mapOf(
            "score" to score,
            "slopeCalculated" to slopeCalculated,
            "errorPercentage" to errorPercentage,
            "aiFeedback" to aiFeedback,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteLabAttempt(attemptId: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection("lab_attempts").document(attemptId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeTeacherAssignments(userId: String): Flow<List<ClassAssignment>> = flow {
        val path = "class_assignments"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        ClassAssignment::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun createClassAssignment(assignment: ClassAssignment): Result<String> {
        val uid = requireUserId()
        val collectionRef = db.collection("class_assignments")
        val docRef = if (assignment.id.isNotBlank()) {
            collectionRef.document(assignment.id)
        } else {
            collectionRef.document()
        }
        val payload = mapOf(
            "userId" to uid,
            "teacherName" to assignment.teacherName.ifBlank { "Physics Instructor" },
            "classLevel" to assignment.classLevel,
            "experimentId" to assignment.experimentId,
            "experimentTitle" to assignment.experimentTitle,
            "examBoard" to assignment.examBoard,
            "dueDate" to assignment.dueDate,
            "targetAccuracyPercent" to assignment.targetAccuracyPercent,
            "status" to assignment.status,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.set(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun closeClassAssignment(assignmentId: String): Result<Unit> {
        requireUserId()
        val docRef = db.collection("class_assignments").document(assignmentId)
        val updates = mapOf(
            "status" to "closed",
            "updatedAt" to FieldValue.serverTimestamp()
        )
        return try {
            docRef.update(updates).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, docRef.path)
            Result.failure(e)
        }
    }
}
