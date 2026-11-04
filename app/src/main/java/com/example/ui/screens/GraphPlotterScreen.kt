package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.PlottedDataPoint
import com.example.data.model.PracticalExperimentSpec
import com.example.ui.theme.LabBrassGold
import com.example.ui.theme.LabCardEmerald
import com.example.ui.theme.LabDeepSlate
import com.example.ui.theme.LabGridLineMajor
import com.example.ui.theme.LabGridLineMinor
import com.example.ui.theme.LabLaserCyan
import com.example.ui.theme.LabPhosphorGreen
import com.example.ui.theme.LabThermalAmber
import com.example.viewmodel.GraphRegressionResult
import com.example.viewmodel.LabViewModel
import kotlin.math.log10

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GraphPlotterScreen(
    viewModel: LabViewModel,
    isExpanded: Boolean = false
) {
    val selectedExp by viewModel.selectedExperiment.collectAsStateWithLifecycle()
    val points by viewModel.plottedPoints.collectAsStateWithLifecycle()
    val isLogScale by viewModel.isLogScale.collectAsStateWithLifecycle()
    val showBestFit by viewModel.showBestFitLine.collectAsStateWithLifecycle()
    val showTriangle by viewModel.showSlopeTriangle.collectAsStateWithLifecycle()

    val regression = viewModel.computeRegression()

    if (isExpanded) {
        // Side-by-Side Big Screen / Desktop Graph Sheet Layout
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.60f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GraphHeaderAndChips(
                    selectedExp = selectedExp,
                    isLogScale = isLogScale,
                    showBestFit = showBestFit,
                    showTriangle = showTriangle,
                    viewModel = viewModel
                )
                WaecGraphSheetCanvas(
                    points = points,
                    isLogScale = isLogScale,
                    showBestFit = showBestFit,
                    showTriangle = showTriangle,
                    regression = regression,
                    sheetHeight = 440.dp
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.40f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RegressionDeductionCard(selectedExp = selectedExp, regression = regression)
                ManualPointPlotterCard(viewModel = viewModel)
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            GraphHeaderAndChips(
                selectedExp = selectedExp,
                isLogScale = isLogScale,
                showBestFit = showBestFit,
                showTriangle = showTriangle,
                viewModel = viewModel
            )
            WaecGraphSheetCanvas(
                points = points,
                isLogScale = isLogScale,
                showBestFit = showBestFit,
                showTriangle = showTriangle,
                regression = regression,
                sheetHeight = 330.dp
            )
            RegressionDeductionCard(selectedExp = selectedExp, regression = regression)
            ManualPointPlotterCard(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GraphHeaderAndChips(
    selectedExp: PracticalExperimentSpec,
    isLogScale: Boolean,
    showBestFit: Boolean,
    showTriangle: Boolean,
    viewModel: LabViewModel
) {
    Text(
        text = "WAEC / NECO Examination Graph Sheet",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
    Text(
        text = "Plot of ${if (isLogScale) "log(${selectedExp.yLabel})" else selectedExp.yLabel} against ${if (isLogScale) "log(${selectedExp.xLabel})" else selectedExp.xLabel}",
        style = MaterialTheme.typography.bodyMedium,
        color = LabBrassGold
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = showBestFit,
            onClick = { viewModel.toggleBestFitLine() },
            label = { Text("Line of Best Fit") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = LabPhosphorGreen,
                selectedLabelColor = Color(0xFF002912)
            )
        )
        FilterChip(
            selected = showTriangle,
            onClick = { viewModel.toggleSlopeTriangle() },
            label = { Text("Slope Triangle (Δy/Δx)") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = LabBrassGold,
                selectedLabelColor = Color(0xFF2B2000)
            )
        )
        FilterChip(
            selected = isLogScale,
            onClick = { viewModel.toggleLogScale() },
            label = { Text(if (isLogScale) "Log₁₀ Scale Active" else "Linear Scale") },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = LabLaserCyan,
                selectedLabelColor = Color(0xFF002538)
            )
        )
    }
}

@Composable
private fun WaecGraphSheetCanvas(
    points: List<PlottedDataPoint>,
    isLogScale: Boolean,
    showBestFit: Boolean,
    showTriangle: Boolean,
    regression: GraphRegressionResult,
    sheetHeight: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(sheetHeight)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF051812))
            .border(1.5.dp, LabPhosphorGreen.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .testTag("waec_graph_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val padLeft = 48f
            val padBottom = 42f
            val padTop = 24f
            val padRight = 24f

            val plotW = (w - padLeft - padRight).coerceAtLeast(10f)
            val plotH = (h - padTop - padBottom).coerceAtLeast(10f)

            val minorCols = 40
            val minorRows = 30
            for (i in 0..minorCols) {
                val gx = padLeft + (plotW * i / minorCols)
                drawLine(
                    color = if (i % 5 == 0) LabGridLineMajor else LabGridLineMinor,
                    start = Offset(gx, padTop),
                    end = Offset(gx, h - padBottom),
                    strokeWidth = if (i % 5 == 0) 1.6f else 0.8f
                )
            }
            for (j in 0..minorRows) {
                val gy = padTop + (plotH * j / minorRows)
                drawLine(
                    color = if (j % 5 == 0) LabGridLineMajor else LabGridLineMinor,
                    start = Offset(padLeft, gy),
                    end = Offset(w - padRight, gy),
                    strokeWidth = if (j % 5 == 0) 1.6f else 0.8f
                )
            }

            drawLine(
                color = Color.White,
                start = Offset(padLeft, padTop),
                end = Offset(padLeft, h - padBottom),
                strokeWidth = 2.5f
            )
            drawLine(
                color = Color.White,
                start = Offset(padLeft, h - padBottom),
                end = Offset(w - padRight, h - padBottom),
                strokeWidth = 2.5f
            )

            if (points.isNotEmpty()) {
                val transformed = points.map { pt ->
                    val tx = if (isLogScale && pt.xVal > 0) log10(pt.xVal) else pt.xVal
                    val ty = if (isLogScale && pt.yVal > 0) log10(pt.yVal) else pt.yVal
                    Triple(tx, ty, pt.uncertainty)
                }

                val minX = (transformed.minOf { it.first } * 0.90).coerceAtMost(transformed.minOf { it.first } - 0.05)
                val maxX = (transformed.maxOf { it.first } * 1.08).coerceAtLeast(minX + 0.1)
                val minY = (transformed.minOf { it.second } * 0.88).coerceAtMost(transformed.minOf { it.second } - 0.05)
                val maxY = (transformed.maxOf { it.second } * 1.08).coerceAtLeast(minY + 0.1)

                fun toScreenX(vx: Double): Float =
                    (padLeft + ((vx - minX) / (maxX - minX)) * plotW).toFloat()

                fun toScreenY(vy: Double): Float =
                    (h - padBottom - ((vy - minY) / (maxY - minY)) * plotH).toFloat()

                if (showBestFit && points.size >= 2) {
                    val x1 = minX
                    val y1 = regression.slope * x1 + regression.yIntercept
                    val x2 = maxX
                    val y2 = regression.slope * x2 + regression.yIntercept

                    drawLine(
                        color = LabPhosphorGreen,
                        start = Offset(toScreenX(x1), toScreenY(y1).coerceIn(padTop, h - padBottom)),
                        end = Offset(toScreenX(x2), toScreenY(y2).coerceIn(padTop, h - padBottom)),
                        strokeWidth = 3.2f
                    )

                    if (showTriangle) {
                        val triX1 = minX + (maxX - minX) * 0.22
                        val triY1 = regression.slope * triX1 + regression.yIntercept
                        val triX2 = minX + (maxX - minX) * 0.78
                        val triY2 = regression.slope * triX2 + regression.yIntercept

                        val sx1 = toScreenX(triX1)
                        val sy1 = toScreenY(triY1).coerceIn(padTop, h - padBottom)
                        val sx2 = toScreenX(triX2)
                        val sy2 = toScreenY(triY2).coerceIn(padTop, h - padBottom)

                        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
                        drawLine(
                            color = LabBrassGold,
                            start = Offset(sx1, sy1),
                            end = Offset(sx2, sy1),
                            strokeWidth = 2.5f,
                            pathEffect = dashEffect
                        )
                        drawLine(
                            color = LabBrassGold,
                            start = Offset(sx2, sy1),
                            end = Offset(sx2, sy2),
                            strokeWidth = 2.5f,
                            pathEffect = dashEffect
                        )
                    }
                }

                transformed.forEach { (vx, vy, unc) ->
                    val cx = toScreenX(vx)
                    val cy = toScreenY(vy)

                    val errTop = toScreenY(vy + unc).coerceAtLeast(padTop)
                    val errBot = toScreenY(vy - unc).coerceAtMost(h - padBottom)
                    drawLine(
                        color = LabLaserCyan.copy(alpha = 0.7f),
                        start = Offset(cx, errTop),
                        end = Offset(cx, errBot),
                        strokeWidth = 1.8f
                    )

                    val crossRadius = 7f
                    drawLine(
                        color = LabThermalAmber,
                        start = Offset(cx - crossRadius, cy),
                        end = Offset(cx + crossRadius, cy),
                        strokeWidth = 2.5f
                    )
                    drawLine(
                        color = LabThermalAmber,
                        start = Offset(cx, cy - crossRadius),
                        end = Offset(cx, cy + crossRadius),
                        strokeWidth = 2.5f
                    )
                }
            }
        }
    }
}

@Composable
private fun RegressionDeductionCard(
    selectedExp: PracticalExperimentSpec,
    regression: GraphRegressionResult
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
            Text(
                text = "Scientific Graph Deductions & Uncertainty Analysis",
                style = MaterialTheme.typography.titleMedium,
                color = LabBrassGold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Gradient (Slope s)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${"%.4f".format(regression.slope)} ${selectedExp.slopeUnit}",
                        style = MaterialTheme.typography.labelLarge,
                        color = LabPhosphorGreen
                    )
                }
                Column {
                    Text("Y-Intercept (c)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "%.4f".format(regression.yIntercept),
                        style = MaterialTheme.typography.labelLarge,
                        color = LabLaserCyan
                    )
                }
                Column {
                    Text("Error vs Standard", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${"%.2f".format(regression.errorPercent)}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = LabBrassGold
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Slope Uncertainty: ±${"%.4f".format(regression.slopeUncertainty)}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "Correlation R² = ${"%.4f".format(regression.rSquared)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Surface(
                color = LabDeepSlate,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = regression.deducedConstantText.ifBlank { "Plot at least 2 readings to compute physical constants." },
                    style = MaterialTheme.typography.labelLarge,
                    color = LabPhosphorGreen,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun ManualPointPlotterCard(viewModel: LabViewModel) {
    var manualX by remember { mutableStateOf("") }
    var manualY by remember { mutableStateOf("") }

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
                text = "Manual Point Plotter / Table Actions",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = manualX,
                    onValueChange = { manualX = it },
                    label = { Text("X Value") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = manualY,
                    onValueChange = { manualY = it },
                    label = { Text("Y Value") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        val x = manualX.toDoubleOrNull()
                        val y = manualY.toDoubleOrNull()
                        if (x != null && y != null) {
                            viewModel.addManualPoint(x, y)
                            manualX = ""
                            manualY = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LabPhosphorGreen,
                        contentColor = Color(0xFF002912)
                    ),
                    modifier = Modifier.height(52.dp)
                ) {
                    Icon(Icons.Default.AddCircleOutline, contentDescription = "Add Point")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.seedInitialPointsForExperiment() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Sample Data")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Load 6 Readings")
                }
                OutlinedButton(
                    onClick = { viewModel.clearReadings() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = "Clear Graph")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clear Sheet")
                }
            }
        }
    }
}
