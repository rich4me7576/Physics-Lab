package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SolarPower
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.LabCatalogData
import com.example.ui.theme.LabBrassGold
import com.example.ui.theme.LabCardEmerald
import com.example.ui.theme.LabDeepSlate
import com.example.ui.theme.LabLaserCyan
import com.example.ui.theme.LabPhosphorGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HubAndBlueprintScreen() {
    var activeSection by remember { mutableIntStateOf(0) }
    var selectedEquipCategory by remember { mutableStateOf("All") }

    val sections = listOf(
        "1. Curriculum Matrix (10)",
        "2. 3D Equipment (36)",
        "3. Missions & Badges",
        "4.PRD & System Blueprint (15)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Rural School Solar & Offline Readiness Status Banner (Part 7)
        Card(
            colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        Icons.Default.SolarPower,
                        contentDescription = "Rural Solar & Offline Mode",
                        tint = LabBrassGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Rural Africa Offline & Solar Micro-Server Ready",
                            style = MaterialTheme.typography.titleMedium,
                            color = LabPhosphorGreen
                        )
                        Text(
                            text = "100% Local 60Hz Physics & Ray-Tracer • Low-RAM (<38MB LOD Bundle) • Firestore Delta Sync",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Sub-navigation Pills
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sections.forEachIndexed { index, label ->
                FilterChip(
                    selected = activeSection == index,
                    onClick = { activeSection = index },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LabPhosphorGreen,
                        selectedLabelColor = Color(0xFF002912)
                    ),
                    modifier = Modifier.testTag("hub_tab_$index")
                )
            }
        }

        when (activeSection) {
            0 -> {
                // PART 1: Complete Curriculum Alignment Matrix
                Text(
                    text = "Part 1: NERDC, WAEC & NECO Curriculum Coverage Matrix",
                    style = MaterialTheme.typography.headlineMedium,
                    color = LabBrassGold
                )
                LabCatalogData.curriculumMatrix.forEach { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.domain,
                                style = MaterialTheme.typography.titleLarge,
                                color = LabPhosphorGreen
                            )
                            Text(
                                text = "${item.nerdcCode}  |  ${item.waecSyllabusRef}  |  ${item.necoSyllabusRef}",
                                style = MaterialTheme.typography.labelSmall,
                                color = LabBrassGold
                            )
                            Text(
                                text = "Sub-Topics: ${item.subTopics.joinToString(" • ")}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LabLaserCyan
                            )
                            Text(
                                text = "Learning Objectives:",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                            item.learningObjectives.forEach { obj ->
                                Text("• $obj", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(
                                text = "WAEC/NECO Practical Activities:",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                            item.practicalActivities.forEach { act ->
                                Text("• $act", style = MaterialTheme.typography.bodyMedium, color = LabPhosphorGreen)
                            }
                            Text(
                                text = "Required 3D Equipment: ${item.requiredEquipment.joinToString(", ")}",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = "3D Engine Spec: ${item.simulationRequirements}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            1 -> {
                // PART 2: Complete Virtual 3D Equipment Inventory (36 Items)
                Text(
                    text = "Part 2: Complete 3D Virtual Equipment Inventory (36 Items)",
                    style = MaterialTheme.typography.headlineMedium,
                    color = LabBrassGold
                )

                val categories = listOf("All", "Mechanics", "Electricity", "Optics", "Heat", "Waves", "Magnetism")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedEquipCategory == cat,
                            onClick = { selectedEquipCategory = cat },
                            label = { Text(cat) }
                        )
                    }
                }

                val filteredEquip = if (selectedEquipCategory == "All") {
                    LabCatalogData.equipmentInventory
                } else {
                    LabCatalogData.equipmentInventory.filter { it.category == selectedEquipCategory }
                }

                filteredEquip.forEach { eq ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = eq.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = LabPhosphorGreen,
                                    modifier = Modifier.weight(1f)
                                )
                                Surface(
                                    color = LabDeepSlate,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = eq.category,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = LabBrassGold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Dimensions: ${eq.dimensionsCm}  |  Mass: ${eq.massGrams} g  |  Accuracy: ${eq.measurementAccuracy}",
                                style = MaterialTheme.typography.labelSmall,
                                color = LabLaserCyan
                            )
                            Text(
                                text = "Zero-Error Tolerance: ${eq.zeroErrorRange}",
                                style = MaterialTheme.typography.labelSmall,
                                color = LabBrassGold
                            )
                            Text(
                                text = "3D Physics Behaviour: ${eq.physicsBehaviour}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Calibration Procedure: ${eq.calibrationProcedure}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            2 -> {
                // PART 9: Gamification, Physics Missions, Badges & National Leaderboard
                Text(
                    text = "Part 9: Gamification, Physics Missions & National Leaderboard",
                    style = MaterialTheme.typography.headlineMedium,
                    color = LabBrassGold
                )

                LabCatalogData.physicsMissions.forEach { mission ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.EmojiEvents, contentDescription = "Badge", tint = LabBrassGold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = mission.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = LabPhosphorGreen
                                    )
                                }
                                Surface(
                                    color = LabDeepSlate,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "+${mission.xpReward} XP",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = LabBrassGold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Badge Awarded: ${mission.badgeTitle} (Target Error ≤ ${mission.maxAllowedErrorPercent}%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = LabLaserCyan
                            )
                            Text(
                                text = mission.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // National School Leaderboard Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Nigerian Senior Secondary Schools Practical Mastery Leaderboard",
                            style = MaterialTheme.typography.titleMedium,
                            color = LabBrassGold
                        )
                        val ranks = listOf(
                            Triple("1. Federal Government College, Enugu (SS3)", "96.4% WAEC Readiness", "4,850 XP"),
                            Triple("2. Queen's College, Yaba, Lagos (SS3)", "95.8% WAEC Readiness", "4,720 XP"),
                            Triple("3. Rumfa College, Kano (SS3)", "94.9% NECO Readiness", "4,610 XP"),
                            Triple("4. Government Secondary School, Garki, Abuja", "94.1% WAEC Readiness", "4,490 XP"),
                            Triple("5. Community Secondary School, Uyo, Akwa Ibom", "93.5% NECO Readiness", "4,380 XP")
                        )
                        ranks.forEach { (school, readiness, xp) ->
                            Surface(
                                color = LabDeepSlate,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(school, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                        Text(readiness, style = MaterialTheme.typography.labelSmall, color = LabPhosphorGreen)
                                    }
                                    Text(xp, style = MaterialTheme.typography.labelLarge, color = LabBrassGold)
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                // PARTS 10 & 11: Complete 15-Deliverable PRD & Architecture Blueprint Suite
                Text(
                    text = "Parts 10 & 11: Complete 15-Section Product & Software Architecture Blueprint",
                    style = MaterialTheme.typography.headlineMedium,
                    color = LabBrassGold
                )

                LabCatalogData.blueprintDeliverables.forEach { doc ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = LabCardEmerald),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = doc.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = LabPhosphorGreen
                            )
                            Text(
                                text = doc.subtitle,
                                style = MaterialTheme.typography.labelLarge,
                                color = LabBrassGold
                            )
                            doc.keyHighlights.forEach { hl ->
                                Text("✓ $hl", style = MaterialTheme.typography.bodyMedium, color = LabLaserCyan)
                            }
                            Surface(
                                color = LabDeepSlate,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = doc.detailedSpecification,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
