package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CurriculumDomainProgress
import com.example.data.model.ExperimentCompletionSummary
import com.example.data.model.LabAttempt
import com.example.data.model.LabCatalogData
import com.example.ui.theme.LabBrassGold
import com.example.ui.theme.LabCardEmerald
import com.example.ui.theme.LabDeepSlate
import com.example.ui.theme.LabLaserCyan
import com.example.ui.theme.LabPhosphorGreen
import com.example.ui.theme.LabTextSecondary
import com.example.ui.theme.LabThermalAmber
import com.example.viewmodel.LabViewModel
import com.example.viewmodel.UiState

@Composable
fun ProgressDashboardScreen(
    viewModel: LabViewModel,
    isExpanded: Boolean = false,
    onOpenExperimentInLab: () -> Unit = {}
) {
    val userAttemptsState by viewModel.userAttempts.collectAsStateWithLifecycle()
    val sessionSummaries by viewModel.sessionCompletedSummaries.collectAsStateWithLifecycle()
    val completedObjectiveKeys by viewModel.completedObjectiveKeys.collectAsStateWithLifecycle()
    val selectedExperiment by viewModel.selectedExperiment.collectAsStateWithLifecycle()

    val cloudAttempts: List<LabAttempt> = when (val st = userAttemptsState) {
        is UiState.Success -> st.data
        else -> emptyList()
    }

    val experimentSummaries = remember(cloudAttempts, sessionSummaries) {
        viewModel.buildExperimentSummaries(cloudAttempts, sessionSummaries)
    }

    val curriculumProgressList = remember(experimentSummaries, completedObjectiveKeys) {
        viewModel.buildCurriculumProgress(experimentSummaries, completedObjectiveKeys)
    }

    var selectedSectionFilter by rememberSaveable { mutableStateOf("ALL") } // ALL, EXPERIMENTS, CURRICULUM
    var selectedStatusFilter by rememberSaveable { mutableStateOf("ALL") } // ALL, COMPLETED, PENDING, WAEC, NECO
    var inspectedSummary by remember { mutableStateOf<ExperimentCompletionSummary?>(null) }

    val filteredExperiments = remember(experimentSummaries, selectedStatusFilter) {
        experimentSummaries.filter { item ->
            when (selectedStatusFilter) {
                "COMPLETED" -> item.isCompleted
                "PENDING" -> !item.isCompleted
                "WAEC" -> item.examBoard.equals("WAEC", ignoreCase = true)
                "NECO" -> item.examBoard.equals("NECO", ignoreCase = true) || item.examBoard.equals("NERDC", ignoreCase = true)
                else -> true
            }
        }
    }

    val completedCount = experimentSummaries.count { it.isCompleted }
    val totalExperiments = experimentSummaries.size.coerceAtLeast(1)
    val avgScore = experimentSummaries.filter { it.isCompleted }.map { it.bestScore }.average().takeIf { !it.isNaN() } ?: 0.0
    val meanAccuracyError = experimentSummaries.filter { it.isCompleted }.map { it.lowestErrorPercent }.average().takeIf { !it.isNaN() } ?: 0.0
    val totalObjectives = curriculumProgressList.sumOf { it.totalObjectivesCount }.coerceAtLeast(1)
    val completedObjectives = curriculumProgressList.sumOf { it.completedObjectivesCount }
    val overallCurriculumMastery = curriculumProgressList.map { it.masteryPercent }.average().takeIf { !it.isNaN() }?.toFloat() ?: 0f

    // Adaptive GridCell minimum width: 310.dp on expanded/tablet/desktop screens, 280.dp on compact phones
    val adaptiveMinCellWidth = if (isExpanded) 310.dp else 280.dp

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = adaptiveMinCellWidth),
        modifier = Modifier
            .fillMaxSize()
            .testTag("progress_dashboard_grid"),
        contentPadding = PaddingValues(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Full-Span Hero Mastery & Telemetry Summary Card
        item(span = { GridItemSpan(maxLineSpan) }) {
            DashboardHeroSummaryBanner(
                completedExperiments = completedCount,
                totalExperiments = totalExperiments,
                avgScore = avgScore,
                meanErrorPercent = meanAccuracyError,
                completedObjectives = completedObjectives,
                totalObjectives = totalObjectives,
                overallCurriculumMastery = overallCurriculumMastery,
                activeExperimentTitle = selectedExperiment.title,
                isExpanded = isExpanded,
                onLogCurrentLab = { viewModel.recordCurrentExperimentToDashboard(showSnackbar = true) }
            )
        }

        // 2. Full-Span Filter & Section Switcher Bar
        item(span = { GridItemSpan(maxLineSpan) }) {
            DashboardFilterBar(
                selectedSection = selectedSectionFilter,
                onSelectSection = { selectedSectionFilter = it },
                selectedStatus = selectedStatusFilter,
                onSelectStatus = { selectedStatusFilter = it }
            )
        }

        // 3. Section: Completed Experiments Summary Grid
        if (selectedSectionFilter == "ALL" || selectedSectionFilter == "EXPERIMENTS") {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SectionHeaderRow(
                    title = "Completed Experiments & Practical Reports ($completedCount/$totalExperiments Completed)",
                    subtitle = "Responsive summary grid of WAEC/NECO practical slopes, experimental error tolerances, and physical deductions",
                    accentColor = LabPhosphorGreen
                )
            }

            items(
                items = filteredExperiments,
                key = { "exp_${it.experimentId}" }
            ) { summary ->
                ExperimentSummaryGridCard(
                    summary = summary,
                    onInspectReport = { inspectedSummary = summary },
                    onOpenInLab = {
                        val spec = LabCatalogData.practicalExperiments.find { it.id == summary.experimentId }
                        if (spec != null) {
                            viewModel.selectExperiment(spec)
                            onOpenExperimentInLab()
                        }
                    }
                )
            }
        }

        // 4. Section: NERDC / WAEC / NECO Curriculum Objectives Progress Grid
        if (selectedSectionFilter == "ALL" || selectedSectionFilter == "CURRICULUM") {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(4.dp))
                SectionHeaderRow(
                    title = "NERDC / WAEC / NECO Curriculum Objectives Tracker ($completedObjectives/$totalObjectives Mastered)",
                    subtitle = "Track mastery across all 10 Senior Secondary Physics themes — tap any objective to toggle completion",
                    accentColor = LabBrassGold
                )
            }

            items(
                items = curriculumProgressList,
                key = { "curr_${it.curriculumItem.id}" }
            ) { domainProgress ->
                CurriculumProgressGridCard(
                    domainProgress = domainProgress,
                    completedObjectiveKeys = completedObjectiveKeys,
                    onToggleObjective = { objIdx ->
                        viewModel.toggleCurriculumObjective(domainProgress.curriculumItem.id, objIdx)
                    },
                    onPracticeLinkedLab = {
                        val expId = domainProgress.linkedExperimentId
                        val spec = LabCatalogData.practicalExperiments.find { it.id == expId }
                        if (spec != null) {
                            viewModel.selectExperiment(spec)
                            onOpenExperimentInLab()
                        }
                    }
                )
            }
        }
    }

    // Modal for inspecting full completed experiment report
    val currentInspected = inspectedSummary
    if (currentInspected != null) {
        ExperimentReportDetailDialog(
            summary = currentInspected,
            onDismiss = { inspectedSummary = null },
            onLaunchInLab = {
                val spec = LabCatalogData.practicalExperiments.find { it.id == currentInspected.experimentId }
                inspectedSummary = null
                if (spec != null) {
                    viewModel.selectExperiment(spec)
                    onOpenExperimentInLab()
                }
            }
        )
    }
}

@Composable
private fun DashboardHeroSummaryBanner(
    completedExperiments: Int,
    totalExperiments: Int,
    avgScore: Double,
    meanErrorPercent: Double,
    completedObjectives: Int,
    totalObjectives: Int,
    overallCurriculumMastery: Float,
    activeExperimentTitle: String,
    isExpanded: Boolean,
    onLogCurrentLab: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LabPhosphorGreen.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    // Dual Concentric Ring Progress Gauge (Experiments + Curriculum Mastery)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(84.dp)
                    ) {
                        val expFraction = (completedExperiments.toFloat() / totalExperiments.toFloat()).coerceIn(0f, 1f)
                        val currFraction = (overallCurriculumMastery / 100f).coerceIn(0f, 1f)
                        Canvas(modifier = Modifier.size(84.dp)) {
                            val strokeOuter = 8.dp.toPx()
                            val strokeInner = 7.dp.toPx()
                            // Outer track (Curriculum Mastery)
                            drawArc(
                                color = Color(0xFF15382E),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = LabPhosphorGreen,
                                startAngle = -90f,
                                sweepAngle = 360f * currFraction,
                                useCenter = false,
                                style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
                            )
                            // Inner track (Completed Practicals)
                            val inset = 13.dp.toPx()
                            drawArc(
                                color = Color(0xFF15382E),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = Offset(inset, inset),
                                size = Size(size.width - inset * 2, size.height - inset * 2),
                                style = Stroke(width = strokeInner, cap = StrokeCap.Round)
                            )
                            drawArc(
                                color = LabBrassGold,
                                startAngle = -90f,
                                sweepAngle = 360f * expFraction,
                                useCenter = false,
                                topLeft = Offset(inset, inset),
                                size = Size(size.width - inset * 2, size.height - inset * 2),
                                style = Stroke(width = strokeInner, cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${overallCurriculumMastery.toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = LabPhosphorGreen
                            )
                            Text(
                                text = "Mastery",
                                style = MaterialTheme.typography.labelSmall,
                                color = LabBrassGold
                            )
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Curriculum & Practical Mastery Dashboard",
                            style = MaterialTheme.typography.titleMedium,
                            color = LabPhosphorGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "NERDC Senior Secondary Physics • WAEC Paper 3 & NECO Practical Tracker",
                            style = MaterialTheme.typography.bodySmall,
                            color = LabBrassGold
                        )
                        Text(
                            text = "Active Workbench: ${activeExperimentTitle.substringBefore("—").trim()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = LabLaserCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (isExpanded) {
                    Button(
                        onClick = onLogCurrentLab,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LabPhosphorGreen,
                            contentColor = Color(0xFF002912)
                        ),
                        modifier = Modifier.testTag("log_current_lab_dashboard_button")
                    ) {
                        Icon(Icons.Default.FactCheck, contentDescription = "Log Active Lab")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Log Active Lab to Dashboard", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Responsive KPI Metric Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiMetricTile(
                    label = "Practicals Done",
                    value = "$completedExperiments / $totalExperiments",
                    subValue = "${(completedExperiments * 100 / totalExperiments)}% Syllabus Labs",
                    accentColor = LabPhosphorGreen,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricTile(
                    label = "Mean WAEC Score",
                    value = "${"%.1f".format(avgScore)}%",
                    subValue = if (avgScore >= 85) "A1 Distinction" else "B2 Very Good",
                    accentColor = LabBrassGold,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricTile(
                    label = "Mean Slope Error",
                    value = "±${"%.2f".format(meanErrorPercent)}%",
                    subValue = "Target ≤ ±5.0%",
                    accentColor = LabLaserCyan,
                    modifier = Modifier.weight(1f)
                )
                KpiMetricTile(
                    label = "NERDC Objectives",
                    value = "$completedObjectives / $totalObjectives",
                    subValue = "10 Themes Tracked",
                    accentColor = LabPhosphorGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            if (!isExpanded) {
                Button(
                    onClick = onLogCurrentLab,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LabPhosphorGreen,
                        contentColor = Color(0xFF002912)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("log_current_lab_dashboard_button_compact")
                ) {
                    Icon(Icons.Default.FactCheck, contentDescription = "Log Active Lab")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Active Workbench Lab to Dashboard", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun KpiMetricTile(
    label: String,
    value: String,
    subValue: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = LabDeepSlate,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.border(1.dp, accentColor.copy(alpha = 0.28f), RoundedCornerShape(10.dp))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = LabTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                maxLines = 1
            )
            Text(
                text = subValue,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DashboardFilterBar(
    selectedSection: String,
    onSelectSection: (String) -> Unit,
    selectedStatus: String,
    onSelectStatus: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val sectionOptions = listOf(
            "ALL" to "All Sections",
            "EXPERIMENTS" to "Completed Experiments",
            "CURRICULUM" to "Curriculum Objectives"
        )
        sectionOptions.forEach { (key, label) ->
            FilterChip(
                selected = selectedSection == key,
                onClick = { onSelectSection(key) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LabPhosphorGreen,
                    selectedLabelColor = Color(0xFF002912)
                ),
                modifier = Modifier.testTag("dashboard_section_chip_$key")
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        val statusOptions = listOf(
            "ALL" to "All Labs",
            "COMPLETED" to "Completed Only",
            "PENDING" to "In Progress",
            "WAEC" to "WAEC",
            "NECO" to "NECO / NERDC"
        )
        statusOptions.forEach { (key, label) ->
            FilterChip(
                selected = selectedStatus == key,
                onClick = { onSelectStatus(key) },
                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LabBrassGold,
                    selectedLabelColor = Color(0xFF261900)
                ),
                modifier = Modifier.testTag("dashboard_filter_chip_$key")
            )
        }
    }
}

@Composable
private fun SectionHeaderRow(
    title: String,
    subtitle: String,
    accentColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = accentColor,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = LabTextSecondary
        )
    }
}

@Composable
private fun ExperimentSummaryGridCard(
    summary: ExperimentCompletionSummary,
    onInspectReport: () -> Unit,
    onOpenInLab: () -> Unit
) {
    val borderColor = if (summary.isCompleted) {
        LabPhosphorGreen.copy(alpha = 0.45f)
    } else {
        LabBrassGold.copy(alpha = 0.35f)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .testTag("experiment_summary_card_${summary.experimentId}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top badges row: Category + Exam Board + Grade badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = LabDeepSlate,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "${summary.examBoard} • ${summary.category}",
                            style = MaterialTheme.typography.labelSmall,
                            color = LabLaserCyan,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    if (summary.attemptsCount > 0) {
                        Text(
                            text = "${summary.attemptsCount}x runs",
                            style = MaterialTheme.typography.labelSmall,
                            color = LabTextSecondary
                        )
                    }
                }

                Surface(
                    color = if (summary.isCompleted) LabPhosphorGreen.copy(alpha = 0.18f) else LabThermalAmber.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (summary.isCompleted) Icons.Default.TaskAlt else Icons.Default.Science,
                            contentDescription = summary.masteryGrade,
                            tint = if (summary.isCompleted) LabPhosphorGreen else LabBrassGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = summary.masteryGrade,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (summary.isCompleted) LabPhosphorGreen else LabBrassGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Experiment Title
            Text(
                text = summary.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Score + Slope + Error Metrics Box
            Surface(
                color = LabDeepSlate,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Best WAEC Score",
                                style = MaterialTheme.typography.labelSmall,
                                color = LabTextSecondary
                            )
                            Text(
                                text = if (summary.isCompleted) "${summary.bestScore} / 100" else "Not Graded",
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (summary.isCompleted) LabPhosphorGreen else LabBrassGold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Slope & Error",
                                style = MaterialTheme.typography.labelSmall,
                                color = LabTextSecondary
                            )
                            Text(
                                text = if (summary.isCompleted) {
                                    "s=${"%.3f".format(summary.bestSlope)} (±${"%.2f".format(summary.lowestErrorPercent)}%)"
                                } else {
                                    "Target s=${summary.targetSlope}"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = LabLaserCyan
                            )
                        }
                    }

                    // Visual Score / Accuracy Bar
                    LinearProgressIndicator(
                        progress = {
                            if (summary.isCompleted) {
                                (summary.bestScore / 100f).coerceIn(0.05f, 1f)
                            } else {
                                0.08f
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (summary.isCompleted) LabPhosphorGreen else LabBrassGold,
                        trackColor = Color(0xFF15382E)
                    )

                    Text(
                        text = summary.deducedConstantSummary,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = LabBrassGold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Linked Curriculum Domain & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Curriculum: ${summary.linkedCurriculumDomain}",
                    style = MaterialTheme.typography.labelSmall,
                    color = LabTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = summary.lastCompletedLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (summary.isCompleted) LabPhosphorGreen else LabTextSecondary
                )
            }

            // Action Buttons: Inspect Report & Open in 3D Lab
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onInspectReport,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("inspect_report_${summary.experimentId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Analytics,
                        contentDescription = "Inspect Report",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Report", style = MaterialTheme.typography.labelSmall)
                }

                Button(
                    onClick = onOpenInLab,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LabPhosphorGreen,
                        contentColor = Color(0xFF002912)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("launch_lab_${summary.experimentId}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Open in 3D Lab",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (summary.isCompleted) "Re-Run Lab" else "Start Lab",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CurriculumProgressGridCard(
    domainProgress: CurriculumDomainProgress,
    completedObjectiveKeys: Set<String>,
    onToggleObjective: (Int) -> Unit,
    onPracticeLinkedLab: () -> Unit
) {
    val item = domainProgress.curriculumItem
    val masteryPct = domainProgress.masteryPercent
    val progressColor = when {
        masteryPct >= 80f -> LabPhosphorGreen
        masteryPct >= 50f -> LabBrassGold
        else -> LabLaserCyan
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, progressColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .testTag("curriculum_card_${item.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Domain Title & Mastery % Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.domain,
                        style = MaterialTheme.typography.titleSmall,
                        color = progressColor,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.nerdcCode} • ${item.waecSyllabusRef}",
                        style = MaterialTheme.typography.labelSmall,
                        color = LabTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = LabDeepSlate,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${masteryPct.toInt()}% Mastered",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = progressColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Progress Bar + Counts Row
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LinearProgressIndicator(
                    progress = { (masteryPct / 100f).coerceIn(0.04f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = progressColor,
                    trackColor = LabDeepSlate
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Objectives: ${domainProgress.completedObjectivesCount}/${domainProgress.totalObjectivesCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = LabPhosphorGreen
                    )
                    Text(
                        text = "Practicals: ${domainProgress.completedPracticalsCount}/${domainProgress.totalPracticalsCount}",
                        style = MaterialTheme.typography.labelSmall,
                        color = LabBrassGold
                    )
                }
            }

            // Interactive Learning Objectives Checklist
            Surface(
                color = LabDeepSlate,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "NERDC Learning Objectives (Tap to toggle mastery):",
                        style = MaterialTheme.typography.labelSmall,
                        color = LabBrassGold,
                        fontWeight = FontWeight.Bold
                    )
                    item.learningObjectives.forEachIndexed { idx, objectiveText ->
                        val isChecked = completedObjectiveKeys.contains("${item.id}:$idx")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onToggleObjective(idx) }
                                .padding(vertical = 3.dp)
                                .testTag("objective_toggle_${item.id}_$idx"),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (isChecked) "Mastered" else "In Progress",
                                tint = if (isChecked) LabPhosphorGreen else LabTextSecondary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 1.dp)
                            )
                            Text(
                                text = objectiveText,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isChecked) {
                                    MaterialTheme.colorScheme.onSurface
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                                }
                            )
                        }
                    }
                }
            }

            // Subtopics summary & Practice in 3D Lab button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Topics: ${item.subTopics.take(3).joinToString(", ")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = LabTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onPracticeLinkedLab,
                    modifier = Modifier.testTag("curriculum_practice_lab_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Practice Linked 3D Lab",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("3D Practical", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun ExperimentReportDetailDialog(
    summary: ExperimentCompletionSummary,
    onDismiss: () -> Unit,
    onLaunchInLab: () -> Unit
) {
    val fullSpec = LabCatalogData.practicalExperiments.find { it.id == summary.experimentId }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LabCardEmerald,
        title = {
            Column {
                Text(
                    text = summary.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = LabPhosphorGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${summary.examBoard} Practical Report • Grade: ${summary.masteryGrade}",
                    style = MaterialTheme.typography.labelSmall,
                    color = LabBrassGold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = LabDeepSlate,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Best Score: ${summary.bestScore}/100 (${summary.attemptsCount} Attempts)",
                            style = MaterialTheme.typography.labelLarge,
                            color = LabPhosphorGreen
                        )
                        Text(
                            text = "Calculated Slope: ${"%.4f".format(summary.bestSlope)} ${summary.slopeUnit} (Target: ${summary.targetSlope})",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = LabLaserCyan
                        )
                        Text(
                            text = "Experimental Error: ±${"%.2f".format(summary.lowestErrorPercent)}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = LabBrassGold
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = LabCardEmerald)
                        Text(
                            text = summary.deducedConstantSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                if (fullSpec != null) {
                    Text(
                        text = "Aim: ${fullSpec.aim}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Theory: ${fullSpec.theoryFormula}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = LabLaserCyan
                    )
                    Text(
                        text = "Standard WAEC/NECO Precautions Verified:",
                        style = MaterialTheme.typography.labelMedium,
                        color = LabBrassGold,
                        fontWeight = FontWeight.Bold
                    )
                    fullSpec.standardPrecautions.take(3).forEachIndexed { idx, prec ->
                        Text(
                            text = "${idx + 1}. $prec",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onLaunchInLab,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LabPhosphorGreen,
                    contentColor = Color(0xFF002912)
                )
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Open in 3D Lab")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Open in 3D Lab", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
