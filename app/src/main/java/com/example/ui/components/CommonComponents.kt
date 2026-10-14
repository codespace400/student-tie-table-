package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.CoursePalette
import com.example.data.model.PastelCourseColor
import java.util.Locale

@Composable
fun PastelColorSwatchPicker(
    selectedColorIndex: Int,
    onColorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Course Color Theme",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CoursePalette.colors.forEachIndexed { index, pastel ->
                val isSelected = index == selectedColorIndex
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(pastel.backgroundHex))
                        .border(
                            width = if (isSelected) 3.dp else 1.5.dp,
                            color = if (isSelected) Color(pastel.accentHex) else Color(pastel.borderHex),
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(index) }
                        .testTag("color_swatch_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected color ${pastel.name}",
                            tint = Color(pastel.accentHex),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        val currentColor = CoursePalette.getColor(selectedColorIndex)
        Text(
            text = currentColor.name,
            style = MaterialTheme.typography.bodySmall,
            color = Color(currentColor.accentHex),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun DayPickerMultiSelect(
    selectedDays: List<Int>,
    onDayToggled: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Day(s) of Week",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            dayNames.forEachIndexed { index, name ->
                val dayNumber = index + 1
                val isSelected = selectedDays.contains(dayNumber)
                val primaryColor = MaterialTheme.colorScheme.primary

                Surface(
                    onClick = { onDayToggled(dayNumber) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) primaryColor else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("day_picker_day_$dayNumber")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimePickerCard(
    label: String,
    hour: Int,
    minute: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("time_picker_$label")
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = Course.formatTime(hour, minute),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SimpleTimePickerDialog(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }
    var isPm by remember { mutableStateOf(initialHour >= 12) }

    val display12Hour = when {
        selectedHour == 0 -> 12
        selectedHour > 12 -> selectedHour - 12
        else -> selectedHour
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // AM / PM Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilterChip(
                        selected = !isPm,
                        onClick = {
                            if (isPm) {
                                isPm = false
                                if (selectedHour >= 12) selectedHour -= 12
                            }
                        },
                        label = { Text("AM", fontWeight = FontWeight.Bold) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    FilterChip(
                        selected = isPm,
                        onClick = {
                            if (!isPm) {
                                isPm = true
                                if (selectedHour < 12) selectedHour += 12
                            }
                        },
                        label = { Text("PM", fontWeight = FontWeight.Bold) }
                    )
                }

                // Hour Stepper
                Text(
                    text = "Hour: $display12Hour",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            var h = display12Hour - 1
                            if (h < 1) h = 12
                            selectedHour = if (isPm) (if (h == 12) 12 else h + 12) else (if (h == 12) 0 else h)
                        }
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease hour")
                    }

                    Slider(
                        value = display12Hour.toFloat(),
                        onValueChange = { floatVal ->
                            val h = floatVal.toInt().coerceIn(1, 12)
                            selectedHour = if (isPm) (if (h == 12) 12 else h + 12) else (if (h == 12) 0 else h)
                        },
                        valueRange = 1f..12f,
                        steps = 10,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            var h = display12Hour + 1
                            if (h > 12) h = 1
                            selectedHour = if (isPm) (if (h == 12) 12 else h + 12) else (if (h == 12) 0 else h)
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase hour")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Minute Stepper (00, 15, 30, 45 or 5-min intervals)
                Text(
                    text = String.format(Locale.getDefault(), "Minute: %02d", selectedMinute),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0, 15, 30, 45).forEach { min ->
                        val isMinSelected = selectedMinute == min
                        Surface(
                            onClick = { selectedMinute = min },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isMinSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = String.format(Locale.getDefault(), ":%02d", min),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isMinSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (isMinSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedHour, selectedMinute) },
                modifier = Modifier.testTag("confirm_time_button")
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RoomDirectionsDialog(
    course: Course,
    onDismiss: () -> Unit
) {
    val pastel = CoursePalette.getColor(course.colorIndex)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(pastel.backgroundHex)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = Color(pastel.accentHex),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Campus Directions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${course.code} • ${course.classroom}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Apartment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Building",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = course.building.ifBlank { "Main Campus Complex" },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 26.dp, top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Layers,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Floor & Room",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${course.floor.ifBlank { "Floor 1" }} • ${course.classroom}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 26.dp, top = 2.dp)
                        )
                    }
                }

                Surface(
                    color = Color(pastel.backgroundHex).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(pastel.borderHex))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Signpost,
                                contentDescription = null,
                                tint = Color(pastel.accentHex),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Walking Instructions",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color(pastel.accentHex),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = course.directions.ifBlank { "Take the main elevator or stairs to the specified floor and follow hall signs to ${course.classroom}." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_directions_button")
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    courseCode: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Course?",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Are you sure you want to remove $courseCode from your timetable? This action cannot be undone.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.testTag("confirm_delete_button")
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
