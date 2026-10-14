package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Course
import com.example.ui.components.DayPickerMultiSelect
import com.example.ui.components.PastelColorSwatchPicker
import com.example.ui.components.SimpleTimePickerDialog
import com.example.ui.components.TimePickerCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCourseScreen(
    course: Course,
    onSaveCourse: (Course) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(course.name) }
    var code by remember { mutableStateOf(course.code) }
    var lecturer by remember { mutableStateOf(course.lecturer) }
    var lecturerEmail by remember { mutableStateOf(course.lecturerEmail) }
    var lecturerPhone by remember { mutableStateOf(course.lecturerPhone) }
    var classroom by remember { mutableStateOf(course.classroom) }
    var building by remember { mutableStateOf(course.building) }
    var floor by remember { mutableStateOf(course.floor) }
    var directions by remember { mutableStateOf(course.directions) }
    var selectedDays by remember { mutableStateOf(course.daysOfWeek.ifEmpty { listOf(1) }) }
    var startHour by remember { mutableIntStateOf(course.startHour) }
    var startMinute by remember { mutableIntStateOf(course.startMinute) }
    var endHour by remember { mutableIntStateOf(course.endHour) }
    var endMinute by remember { mutableIntStateOf(course.endMinute) }
    var colorIndex by remember { mutableIntStateOf(course.colorIndex) }
    var recurrence by remember { mutableStateOf(course.recurrence) }
    var notes by remember { mutableStateOf(course.notes) }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isNew = course.id == 0L

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isNew) "Add New Course" else "Edit Course",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("add_edit_back_button")
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (code.isBlank()) {
                                errorMessage = "Please enter a course code (e.g. CS 201)"
                                return@IconButton
                            }
                            if (name.isBlank()) {
                                errorMessage = "Please enter course title"
                                return@IconButton
                            }
                            if (classroom.isBlank()) {
                                errorMessage = "Please enter classroom / room number"
                                return@IconButton
                            }
                            if (selectedDays.isEmpty()) {
                                errorMessage = "Select at least one day of the week"
                                return@IconButton
                            }

                            val updatedCourse = course.copy(
                                name = name.trim(),
                                code = code.trim(),
                                lecturer = lecturer.trim(),
                                lecturerEmail = lecturerEmail.trim(),
                                lecturerPhone = lecturerPhone.trim(),
                                classroom = classroom.trim(),
                                building = building.trim(),
                                floor = floor.trim(),
                                directions = directions.trim(),
                                daysOfWeek = selectedDays.sorted(),
                                startHour = startHour,
                                startMinute = startMinute,
                                endHour = endHour,
                                endMinute = endMinute,
                                colorIndex = colorIndex,
                                recurrence = recurrence,
                                notes = notes.trim()
                            )
                            onSaveCourse(updatedCourse)
                        },
                        modifier = Modifier.testTag("save_course_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Course",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            errorMessage?.let { error ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Course Identification Section
            OutlinedCard(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Course Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it; errorMessage = null },
                        label = { Text("Course Code *") },
                        placeholder = { Text("e.g. CS 201, MATH 152") },
                        leadingIcon = { Icon(Icons.Outlined.Bookmark, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_course_code")
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = null },
                        label = { Text("Course Name *") },
                        placeholder = { Text("e.g. Data Structures & Algorithms") },
                        leadingIcon = { Icon(Icons.Outlined.School, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_course_name")
                    )
                }
            }

            // Schedule & Time Section
            OutlinedCard(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Schedule & Timing",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    DayPickerMultiSelect(
                        selectedDays = selectedDays,
                        onDayToggled = { day ->
                            selectedDays = if (selectedDays.contains(day)) {
                                if (selectedDays.size > 1) selectedDays - day else selectedDays
                            } else {
                                selectedDays + day
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            TimePickerCard(
                                label = "Start Time",
                                hour = startHour,
                                minute = startMinute,
                                onClick = { showStartTimePicker = true }
                            )
                        }

                        Box(modifier = Modifier.weight(1f)) {
                            TimePickerCard(
                                label = "End Time",
                                hour = endHour,
                                minute = endMinute,
                                onClick = { showEndTimePicker = true }
                            )
                        }
                    }

                    // Recurrence Toggle
                    Column {
                        Text(
                            text = "Recurrence",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Weekly", "Bi-weekly", "Single").forEach { option ->
                                val isSelected = recurrence == option
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { recurrence = option },
                                    label = { Text(option) },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("recurrence_chip_$option")
                                )
                            }
                        }
                    }
                }
            }

            // Location & Room Section
            OutlinedCard(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Location & Classroom",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = classroom,
                        onValueChange = { classroom = it; errorMessage = null },
                        label = { Text("Classroom / Room *") },
                        placeholder = { Text("e.g. Room 302, Hall A") },
                        leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_course_classroom")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = building,
                            onValueChange = { building = it },
                            label = { Text("Building") },
                            placeholder = { Text("Science Pavilion") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = floor,
                            onValueChange = { floor = it },
                            label = { Text("Floor") },
                            placeholder = { Text("3rd Floor") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = directions,
                        onValueChange = { directions = it },
                        label = { Text("Walking Directions / Campus Tips") },
                        placeholder = { Text("Take west stairs near cafeteria...") },
                        maxLines = 3,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Lecturer Information Section
            OutlinedCard(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Lecturer & Contact",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    OutlinedTextField(
                        value = lecturer,
                        onValueChange = { lecturer = it },
                        label = { Text("Lecturer Name") },
                        placeholder = { Text("Dr. Alan Turing") },
                        leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_course_lecturer")
                    )

                    OutlinedTextField(
                        value = lecturerEmail,
                        onValueChange = { lecturerEmail = it },
                        label = { Text("Lecturer Email") },
                        placeholder = { Text("a.turing@university.edu") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = lecturerPhone,
                        onValueChange = { lecturerPhone = it },
                        label = { Text("Lecturer Phone / Office Ext") },
                        placeholder = { Text("+1 (555) 019-2834") },
                        leadingIcon = { Icon(Icons.Outlined.Phone, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Color Theme Section
            OutlinedCard(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    PastelColorSwatchPicker(
                        selectedColorIndex = colorIndex,
                        onColorSelected = { colorIndex = it }
                    )
                }
            }

            // Notes Section
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Additional Notes & Syllabus Reminders") },
                placeholder = { Text("Textbooks required, quiz policies, office hours...") },
                maxLines = 4,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Save Button
            Button(
                onClick = {
                    if (code.isBlank()) {
                        errorMessage = "Please enter a course code (e.g. CS 201)"
                        return@Button
                    }
                    if (name.isBlank()) {
                        errorMessage = "Please enter course title"
                        return@Button
                    }
                    if (classroom.isBlank()) {
                        errorMessage = "Please enter classroom / room number"
                        return@Button
                    }
                    if (selectedDays.isEmpty()) {
                        errorMessage = "Select at least one day of the week"
                        return@Button
                    }

                    val updatedCourse = course.copy(
                        name = name.trim(),
                        code = code.trim(),
                        lecturer = lecturer.trim(),
                        lecturerEmail = lecturerEmail.trim(),
                        lecturerPhone = lecturerPhone.trim(),
                        classroom = classroom.trim(),
                        building = building.trim(),
                        floor = floor.trim(),
                        directions = directions.trim(),
                        daysOfWeek = selectedDays.sorted(),
                        startHour = startHour,
                        startMinute = startMinute,
                        endHour = endHour,
                        endMinute = endMinute,
                        colorIndex = colorIndex,
                        recurrence = recurrence,
                        notes = notes.trim()
                    )
                    onSaveCourse(updatedCourse)
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_course_bottom_button")
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isNew) "Add to Timetable" else "Save Changes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Time Picker Dialogs
    if (showStartTimePicker) {
        SimpleTimePickerDialog(
            title = "Set Class Start Time",
            initialHour = startHour,
            initialMinute = startMinute,
            onDismiss = { showStartTimePicker = false },
            onConfirm = { h, m ->
                startHour = h
                startMinute = m
                showStartTimePicker = false
                // Auto adjust end time if before start time
                if (endHour < h || (endHour == h && endMinute <= m)) {
                    endHour = (h + 1).coerceAtMost(23)
                    endMinute = m
                }
            }
        )
    }

    if (showEndTimePicker) {
        SimpleTimePickerDialog(
            title = "Set Class End Time",
            initialHour = endHour,
            initialMinute = endMinute,
            onDismiss = { showEndTimePicker = false },
            onConfirm = { h, m ->
                endHour = h
                endMinute = m
                showEndTimePicker = false
            }
        )
    }
}
