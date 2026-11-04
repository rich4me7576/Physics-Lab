package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ClassAssignment
import com.example.data.model.LabAttempt
import com.example.ui.theme.LabBrassGold
import com.example.ui.theme.LabCardEmerald
import com.example.ui.theme.LabDeepSlate
import com.example.ui.theme.LabLaserCyan
import com.example.ui.theme.LabPhosphorGreen
import com.example.viewmodel.LabViewModel
import com.example.viewmodel.UiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExamSimulatorScreen(
    viewModel: LabViewModel
) {
    val selectedExp by viewModel.selectedExperiment.collectAsStateWithLifecycle()
    val candidateNumber by viewModel.candidateNumber.collectAsStateWithLifecycle()
    val examBoard by viewModel.examBoard.collectAsStateWithLifecycle()
    val timerRunning by viewModel.examTimerRunning.collectAsStateWithLifecycle()
    val secondsLeft by viewModel.examSecondsRemaining.collectAsStateWithLifecycle()
    val precautions by viewModel.studentPrecautions.collectAsStateWithLifecycle()
    val shortAnswer by viewModel.studentShortAnswer.collectAsStateWithLifecycle()
    val attemptsState by viewModel.userAttempts.collectAsStateWithLifecycle()
    val assignmentsState by viewModel.teacherAssignments.collectAsStateWithLifecycle()

    val (liveScore, liveRubricSummary) = viewModel.calculateAutomatedExamScore()

    var selectedClassLevel by remember { mutableStateOf("SS3") }
    var assignmentDueDate by remember { mutableStateOf("2026-11-30") }

    val hrs = secondsLeft / 3600
    val mins = (secondsLeft % 3600) / 60
    val secs = secondsLeft % 60
    val formattedTimer = "%02d:%02d:%02d".format(hrs, mins, secs)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Candidate & Timed Exam Header Card
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
                    Column {
                        Text(
                            text = "$examBoard Practical Paper 3 Simulator",
                            style = MaterialTheme.typography.titleLarge,
                            color = LabBrassGold
                        )
                        Text(
                            text = "Candidate No: $candidateNumber",
                            style = MaterialTheme.typography.labelLarge,
                            color = LabPhosphorGreen
                        )
                    }
                    Surface(
                        color = LabDeepSlate,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = "Exam Timer",
                                tint = if (timerRunning) LabPhosphorGreen else LabBrassGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = formattedTimer,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                        }
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("WAEC", "NECO", "NERDC").forEach { board ->
                        FilterChip(
                            selected = examBoard == board,
                            onClick = { viewModel.setExamBoard(board) },
                            label = { Text("$board Standard") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LabPhosphorGreen,
                                selectedLabelColor = Color(0xFF002912)
                            )
                        )
                    }
                    OutlinedButton(onClick = { viewModel.toggleExamTimer() }) {
                        Text(if (timerRunning) "Pause Timer" else "Start Timed Exam")
                    }
                    OutlinedButton(onClick = { viewModel.resetExamTimer() }) {
                        Text("Reset")
                    }
                }
            }
        }

        // Digital Answer Booklet Card
        Card(
            colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Digital Practical Answer Booklet — ${selectedExp.title}",
                    style = MaterialTheme.typography.titleMedium,
                    color = LabLaserCyan
                )

                OutlinedTextField(
                    value = candidateNumber,
                    onValueChange = { viewModel.updateCandidateNumber(it) },
                    label = { Text("10-Digit WAEC/NECO Candidate Index Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = selectedExp.shortAnswerQuestions.firstOrNull() ?: "State two precautions taken to ensure accurate results.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = precautions,
                    onValueChange = { viewModel.updateStudentPrecautions(it) },
                    label = { Text("Precautions (State in past tense as required by Chief Examiner)") },
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("precautions_input")
                )

                Text(
                    text = selectedExp.shortAnswerQuestions.getOrNull(1) ?: "Explain the underlying physical principle of this experiment.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedTextField(
                    value = shortAnswer,
                    onValueChange = { viewModel.updateStudentShortAnswer(it) },
                    label = { Text("Short-Answer Theoretical Response") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("short_answer_input")
                )

                // Live Automated Examiner Rubric Score Preview
                Surface(
                    color = LabDeepSlate,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Automated Examiner Score: $liveScore / 100",
                            style = MaterialTheme.typography.titleMedium,
                            color = LabPhosphorGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = liveRubricSummary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { viewModel.submitExamAttemptToCloud() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LabPhosphorGreen,
                        contentColor = Color(0xFF002912)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_exam_cloud_button")
                ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = "Save Practical")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Grade & Submit Booklet to Cloud Firestore",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Teacher Dashboard & Class Practical Assignment Creator (Part 8)
        Card(
            colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.School, contentDescription = "Teacher Dashboard", tint = LabBrassGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Teacher Dashboard & WAEC/NECO Readiness Assignments",
                        style = MaterialTheme.typography.titleMedium,
                        color = LabBrassGold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("SS1", "SS2", "SS3").forEach { cls ->
                        FilterChip(
                            selected = selectedClassLevel == cls,
                            onClick = { selectedClassLevel = cls },
                            label = { Text(cls) }
                        )
                    }
                    OutlinedTextField(
                        value = assignmentDueDate,
                        onValueChange = { assignmentDueDate = it },
                        label = { Text("Due Date") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = {
                        viewModel.createTeacherAssignment(
                            classLevel = selectedClassLevel,
                            experimentSpec = selectedExp,
                            examBoard = examBoard,
                            dueDate = assignmentDueDate,
                            targetAccuracy = 5L
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LabLaserCyan,
                        contentColor = Color(0xFF002538)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_teacher_assignment_button")
                ) {
                    Icon(Icons.Default.AssignmentTurnedIn, contentDescription = "Assign")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Assign '${selectedExp.category}' Practical to $selectedClassLevel")
                }

                when (val state = assignmentsState) {
                    is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    is UiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                    is UiState.Success -> {
                        if (state.data.isEmpty()) {
                            Text(
                                text = "No active class assignments yet. Use the button above to publish an SS1–SS3 practical task.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            state.data.forEach { ass: ClassAssignment ->
                                Surface(
                                    color = LabDeepSlate,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "[${ass.classLevel} • ${ass.examBoard}] ${ass.experimentTitle}",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = LabPhosphorGreen
                                            )
                                            Text(
                                                text = "Due: ${ass.dueDate} | Max Error: ±${ass.targetAccuracyPercent}% | Status: ${ass.status.uppercase()}",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                        if (ass.status == "active" && ass.id.isNotBlank()) {
                                            OutlinedButton(
                                                onClick = { viewModel.closeAssignment(ass.id) }
                                            ) {
                                                Text("Close", style = MaterialTheme.typography.labelSmall)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Cloud Saved Practical Attempts History
        Card(
            colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Submitted WAEC / NECO Practical Booklets (Cloud Synced)",
                    style = MaterialTheme.typography.titleMedium,
                    color = LabPhosphorGreen
                )

                when (val state = attemptsState) {
                    is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    is UiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                    is UiState.Success -> {
                        if (state.data.isEmpty()) {
                            Text(
                                text = "No submitted practical booklets yet. Complete your readings and click 'Grade & Submit Booklet' above.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            state.data.forEach { att: LabAttempt ->
                                Surface(
                                    color = LabDeepSlate,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${att.examBoard} • ${att.category} (Cand: ${att.candidateNumber})",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = LabBrassGold
                                            )
                                            Text(
                                                text = "${att.score}/${att.maxScore}",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = LabPhosphorGreen
                                            )
                                        }
                                        Text(
                                            text = att.experimentTitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Slope: ${"%.4f".format(att.slopeCalculated)} | Error: ${"%.2f".format(att.errorPercentage)}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = LabLaserCyan
                                        )
                                        Text(
                                            text = att.aiFeedback,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
