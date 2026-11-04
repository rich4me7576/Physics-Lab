package com.example.ui.screens

import android.speech.tts.TextToSpeech
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AssistChip
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.LabBrassGold
import com.example.ui.theme.LabCardEmerald
import com.example.ui.theme.LabDeepSlate
import com.example.ui.theme.LabLaserCyan
import com.example.ui.theme.LabPhosphorGreen
import com.example.viewmodel.AiTutorState
import com.example.viewmodel.LabViewModel
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiTutorScreen(
    viewModel: LabViewModel
) {
    val context = LocalContext.current
    val selectedExp by viewModel.selectedExperiment.collectAsStateWithLifecycle()
    val aiState by viewModel.aiTutorState.collectAsStateWithLifecycle()
    val simpleEnglish by viewModel.simpleEnglishMode.collectAsStateWithLifecycle()

    var questionInput by remember { mutableStateOf("") }
    var ttsReady by remember { mutableStateOf(false) }

    val tts = remember(context) {
        var instance: TextToSpeech? = null
        instance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                instance?.language = Locale.UK
                ttsReady = true
            }
        }
        instance
    }

    DisposableEffect(tts) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Simple English / TTS Controls
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = "AI Physics Tutor",
                            tint = LabPhosphorGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Physics Lab Assistant",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    FilterChip(
                        selected = simpleEnglish,
                        onClick = { viewModel.toggleSimpleEnglishMode() },
                        leadingIcon = {
                            Icon(Icons.Default.Translate, contentDescription = "Language Mode", modifier = Modifier.size(16.dp))
                        },
                        label = { Text(if (simpleEnglish) "Simple English" else "Standard WAEC English") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LabBrassGold,
                            selectedLabelColor = Color(0xFF2B2000)
                        ),
                        modifier = Modifier.testTag("toggle_simple_english_chip")
                    )
                }

                Text(
                    text = "Active Context: ${selectedExp.title}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LabLaserCyan
                )

                Text(
                    text = "Quick Diagnostic Prompts:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val quickPrompts = listOf(
                        "Audit my graph slope, percentage error, and deduced constant",
                        "Grade my stated precautions using the WAEC Chief Examiner rubric",
                        "Explain the physics formula and why we plot these axes",
                        "What are the common sources of experimental error in this practical?"
                    )
                    quickPrompts.forEachIndexed { idx, promptText ->
                        AssistChip(
                            onClick = { viewModel.askAiPhysicsTutor(promptText) },
                            label = { Text(promptText, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.testTag("ai_quick_prompt_$idx")
                        )
                    }
                }
            }
        }

        // Custom Question Input Card
        Card(
            colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = questionInput,
                    onValueChange = { questionInput = it },
                    label = { Text("Ask the AI Lab Tutor about your experiment, calculations, or WAEC questions...") },
                    minLines = 2,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_tutor_question_input")
                )

                Button(
                    onClick = {
                        if (questionInput.isNotBlank()) {
                            viewModel.askAiPhysicsTutor(questionInput)
                        } else {
                            viewModel.askAiPhysicsTutor("Guide me step-by-step on how to achieve <2% error in ${selectedExp.title}.")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LabPhosphorGreen,
                        contentColor = Color(0xFF002912)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("ask_ai_tutor_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Ask AI Tutor")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Consult AI Physics Tutor (Gemini 3.8 Flash)", fontWeight = FontWeight.Bold)
                }
            }
        }

        // AI Response & Text-to-Speech Output Card
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
                        text = "AI Examiner & Tutor Feedback",
                        style = MaterialTheme.typography.titleMedium,
                        color = LabBrassGold
                    )

                    val responseText = when (val s = aiState) {
                        is AiTutorState.Streaming -> s.text
                        is AiTutorState.Success -> s.text
                        else -> ""
                    }

                    if (responseText.isNotBlank() && ttsReady) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(
                                onClick = {
                                    tts?.speak(responseText.take(1500), TextToSpeech.QUEUE_FLUSH, null, "ai_tutor_tts")
                                }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = "Read Aloud", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Speak", style = MaterialTheme.typography.labelSmall)
                            }
                            OutlinedButton(
                                onClick = { tts?.stop() }
                            ) {
                                Icon(Icons.Default.Stop, contentDescription = "Stop Audio", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                when (val s = aiState) {
                    is AiTutorState.Idle -> {
                        Surface(
                            color = LabDeepSlate,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Select any quick diagnostic prompt above or type a question to receive real-time personalized guidance, calculation checks, and WAEC/NECO marking feedback.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                    is AiTutorState.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(12.dp)
                        ) {
                            CircularProgressIndicator(color = LabPhosphorGreen, modifier = Modifier.size(26.dp))
                            Text("Analyzing your laboratory table, slope, and precautions...")
                        }
                    }
                    is AiTutorState.Streaming -> {
                        Surface(
                            color = LabDeepSlate,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = s.text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                    is AiTutorState.Success -> {
                        Surface(
                            color = LabDeepSlate,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = s.text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                    is AiTutorState.Error -> {
                        Surface(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = s.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
