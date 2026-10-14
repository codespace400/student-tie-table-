package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.CoursePalette
import java.time.LocalDate
import java.time.LocalTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyGridScreen(
    courses: List<Course>,
    weekStartDay: Int, // 1 = Monday, 7 = Sunday
    onDayHeaderTapped: (Int) -> Unit,
    onCourseTapped: (Long) -> Unit,
    onAddCourseClicked: () -> Unit,
    onSwitchToDailyView: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }
    val todayDayOfWeek = today.dayOfWeek.value // 1 = Mon .. 7 = Sun
    val currentTime = remember { LocalTime.now() }

    // Day headers ordered by week start
    val daysOrder = remember(weekStartDay) {
        if (weekStartDay == 7) {
            listOf(7, 1, 2, 3, 4, 5, 6) // Sun to Sat
        } else {
            listOf(1, 2, 3, 4, 5, 6, 7) // Mon to Sun
        }
    }

    val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // Timeline configuration
    val startHour = 8
    val endHour = 20
    val totalHours = endHour - startHour
    val hourHeight = 64.dp
    val timeColumnWidth = 54.dp
    val dayColumnWidth = 118.dp

    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ClassMate",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            ) {
                                Text(
                                    text = "Weekly Grid",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Tap any day header to view daily schedule",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSwitchToDailyView,
                        modifier = Modifier.testTag("switch_to_daily_view_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ViewAgenda,
                            contentDescription = "Switch to Daily View",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCourseClicked,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .testTag("weekly_grid_add_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Course")
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Day Headers Row (Sticky at top, horizontally scrollable together with columns)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Corner spacer for sticky time column
                Box(
                    modifier = Modifier
                        .width(timeColumnWidth)
                        .height(58.dp)
                        .border(width = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Days headers row with horizontal sync
                Row(
                    modifier = Modifier
                        .horizontalScroll(horizontalScrollState)
                ) {
                    daysOrder.forEach { dayNum ->
                        val isToday = dayNum == todayDayOfWeek
                        val dayLabel = dayNames[dayNum - 1]

                        Column(
                            modifier = Modifier
                                .width(dayColumnWidth)
                                .height(58.dp)
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                                .background(
                                    if (isToday) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                    else MaterialTheme.colorScheme.surface
                                )
                                .clickable { onDayHeaderTapped(dayNum) }
                                .padding(vertical = 6.dp)
                                .testTag("day_header_$dayNum"),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = dayLabel,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            if (isToday) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
                                    Text(
                                        text = "Today",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = "View",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // Grid Content: Sticky left time column + Scrollable day blocks
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
            ) {
                // Sticky Left Time Slots
                Column(
                    modifier = Modifier
                        .width(timeColumnWidth)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    for (h in startHour..endHour) {
                        Box(
                            modifier = Modifier
                                .width(timeColumnWidth)
                                .height(hourHeight)
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            val hourDisplay = if (h == 12) "12 PM"
                            else if (h > 12) "${h - 12} PM"
                            else "$h AM"

                            Text(
                                text = hourDisplay,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Grid Days Columns (synchronized horizontally)
                Row(
                    modifier = Modifier
                        .horizontalScroll(horizontalScrollState)
                ) {
                    daysOrder.forEach { dayNum ->
                        val isToday = dayNum == todayDayOfWeek
                        val dayCourses = courses.filter { it.daysOfWeek.contains(dayNum) }

                        Box(
                            modifier = Modifier
                                .width(dayColumnWidth)
                                .height(hourHeight * totalHours)
                                .border(
                                    width = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                                .background(
                                    if (isToday) MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                                    else MaterialTheme.colorScheme.background
                                )
                        ) {
                            // Hour lines
                            for (h in 0 until totalHours) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(hourHeight)
                                        .offset(y = hourHeight * h)
                                        .border(
                                            width = 0.5.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                        )
                                )
                            }

                            // Current Time Line (if today)
                            if (isToday && currentTime.hour in startHour..endHour) {
                                val currentMinutesFromStart =
                                    (currentTime.hour - startHour) * 60 + currentTime.minute
                                val currentOffsetDp = (currentMinutesFromStart / 60f) * hourHeight.value

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .offset(y = currentOffsetDp.dp)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }

                            // Render course blocks for this day
                            dayCourses.forEach { course ->
                                val courseStartMins = course.startTotalMinutes()
                                val gridStartMins = startHour * 60

                                val topOffsetMins = (courseStartMins - gridStartMins).coerceAtLeast(0)
                                val topOffsetDp = (topOffsetMins / 60f) * hourHeight.value

                                val durationMins = course.durationMinutes()
                                val blockHeightDp = ((durationMins / 60f) * hourHeight.value)
                                    .coerceAtLeast(42f)

                                val pastel = CoursePalette.getColor(course.colorIndex)

                                Surface(
                                    onClick = { onCourseTapped(course.id) },
                                    shape = RoundedCornerShape(10.dp), // 10px rounded event blocks as requested
                                    color = Color(pastel.backgroundHex),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(Color(pastel.borderHex))
                                    ),
                                    shadowElevation = 1.dp,
                                    modifier = Modifier
                                        .width(dayColumnWidth - 6.dp)
                                        .height(blockHeightDp.dp)
                                        .offset(x = 3.dp, y = topOffsetDp.dp)
                                        .testTag("weekly_course_block_${course.id}_day_$dayNum")
                                ) {
                                    Row(modifier = Modifier.fillMaxSize()) {
                                        // Left accent bar
                                        Box(
                                            modifier = Modifier
                                                .width(4.dp)
                                                .fillMaxHeight()
                                                .background(Color(pastel.accentHex))
                                        )

                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 6.dp, vertical = 4.dp),
                                            verticalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column {
                                                Text(
                                                    text = course.code,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(pastel.textHex),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = course.classroom,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(pastel.textHex).copy(alpha = 0.85f),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    fontSize = 10.sp
                                                )
                                            }

                                            if (blockHeightDp >= 50) {
                                                Text(
                                                    text = "${Course.formatTime24(course.startHour, course.startMinute)}-${Course.formatTime24(course.endHour, course.endMinute)}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color(pastel.textHex).copy(alpha = 0.75f),
                                                    fontSize = 9.sp,
                                                    maxLines = 1
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
    }
}
