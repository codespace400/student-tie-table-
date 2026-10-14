package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.RoomDirectionsDialog
import com.example.ui.theme.ClassMateTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.TimetableViewModel

@Composable
fun MainAppScreen(
    viewModel: TimetableViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Theme resolution
    val isSystemDark = isSystemInDarkTheme()
    val isDarkMode = when (uiState.settings.themeMode) {
        "DARK" -> true
        "LIGHT" -> false
        else -> isSystemDark
    }

    // Observe snackbar messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Hardware back navigation handler
    BackHandler(
        enabled = uiState.currentScreen == AppScreen.COURSE_DETAIL ||
                uiState.currentScreen == AppScreen.ADD_EDIT_COURSE ||
                uiState.currentScreen == AppScreen.DAILY_VIEW ||
                uiState.currentScreen == AppScreen.LECTURER_LIST ||
                uiState.currentScreen == AppScreen.SETTINGS
    ) {
        when (uiState.currentScreen) {
            AppScreen.COURSE_DETAIL, AppScreen.ADD_EDIT_COURSE -> {
                viewModel.navigateTo(AppScreen.WEEKLY_GRID)
            }
            AppScreen.DAILY_VIEW, AppScreen.LECTURER_LIST, AppScreen.SETTINGS -> {
                viewModel.navigateTo(AppScreen.WEEKLY_GRID)
            }
            else -> {}
        }
    }

    ClassMateTheme(darkTheme = isDarkMode) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                // Show bottom bar for primary top-level tabs
                if (uiState.currentScreen != AppScreen.COURSE_DETAIL && uiState.currentScreen != AppScreen.ADD_EDIT_COURSE) {
                    NavigationBar(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .testTag("main_bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = uiState.currentScreen == AppScreen.WEEKLY_GRID,
                            onClick = { viewModel.navigateTo(AppScreen.WEEKLY_GRID) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentScreen == AppScreen.WEEKLY_GRID) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                                    contentDescription = "Weekly Grid"
                                )
                            },
                            label = { Text("Weekly", fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.testTag("nav_item_weekly")
                        )

                        NavigationBarItem(
                            selected = uiState.currentScreen == AppScreen.DAILY_VIEW,
                            onClick = { viewModel.navigateTo(AppScreen.DAILY_VIEW) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentScreen == AppScreen.DAILY_VIEW) Icons.Filled.Today else Icons.Outlined.Today,
                                    contentDescription = "Daily Schedule"
                                )
                            },
                            label = { Text("Daily", fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.testTag("nav_item_daily")
                        )

                        NavigationBarItem(
                            selected = uiState.currentScreen == AppScreen.LECTURER_LIST,
                            onClick = { viewModel.navigateTo(AppScreen.LECTURER_LIST) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentScreen == AppScreen.LECTURER_LIST) Icons.Filled.People else Icons.Outlined.People,
                                    contentDescription = "Faculty Directory"
                                )
                            },
                            label = { Text("Faculty", fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.testTag("nav_item_faculty")
                        )

                        NavigationBarItem(
                            selected = uiState.currentScreen == AppScreen.SETTINGS,
                            onClick = { viewModel.navigateTo(AppScreen.SETTINGS) },
                            icon = {
                                Icon(
                                    imageVector = if (uiState.currentScreen == AppScreen.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            label = { Text("Settings", fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.testTag("nav_item_settings")
                        )
                    }
                }
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = uiState.currentScreen, label = "ScreenTransition") { screen ->
                    when (screen) {
                        AppScreen.WEEKLY_GRID -> {
                            WeeklyGridScreen(
                                courses = uiState.courses,
                                weekStartDay = uiState.settings.weekStartDay,
                                onDayHeaderTapped = { day ->
                                    viewModel.jumpToDayInDailyView(day)
                                },
                                onCourseTapped = { courseId ->
                                    viewModel.viewCourseDetail(courseId)
                                },
                                onAddCourseClicked = {
                                    viewModel.startAddCourse()
                                },
                                onSwitchToDailyView = {
                                    viewModel.navigateTo(AppScreen.DAILY_VIEW)
                                }
                            )
                        }

                        AppScreen.DAILY_VIEW -> {
                            DailyViewScreen(
                                courses = uiState.courses,
                                selectedDayOfWeek = uiState.selectedDayOfWeek,
                                onDaySelected = { day ->
                                    viewModel.selectDay(day)
                                },
                                onCourseTapped = { courseId ->
                                    viewModel.viewCourseDetail(courseId)
                                },
                                onShowDirections = { course ->
                                    viewModel.showDirections(course)
                                },
                                onToggleNotification = { courseId, currentStatus ->
                                    viewModel.toggleNotification(courseId, currentStatus)
                                },
                                onAddCourseClicked = {
                                    viewModel.startAddCourse(presetDay = uiState.selectedDayOfWeek)
                                },
                                onSwitchToWeeklyGrid = {
                                    viewModel.navigateTo(AppScreen.WEEKLY_GRID)
                                }
                            )
                        }

                        AppScreen.LECTURER_LIST -> {
                            LecturerListScreen(
                                courses = uiState.courses,
                                searchQuery = uiState.lecturerSearchQuery,
                                onSearchQueryChange = { query ->
                                    viewModel.setLecturerSearch(query)
                                },
                                onCourseClick = { courseId ->
                                    viewModel.viewCourseDetail(courseId)
                                }
                            )
                        }

                        AppScreen.SETTINGS -> {
                            SettingsScreen(
                                settings = uiState.settings,
                                courses = uiState.courses,
                                onReminderLeadTimeChange = { minutes ->
                                    viewModel.updateReminderLeadTime(minutes)
                                },
                                onWeekStartDayChange = { startDay ->
                                    viewModel.updateWeekStartDay(startDay)
                                },
                                onThemeModeChange = { mode ->
                                    viewModel.updateThemeMode(mode)
                                },
                                onResetSampleData = {
                                    viewModel.resetToSampleData()
                                },
                                onClearAllData = {
                                    viewModel.clearAllData()
                                }
                            )
                        }

                        AppScreen.COURSE_DETAIL -> {
                            val selectedCourse = uiState.courses.find { it.id == uiState.selectedCourseId }
                            if (selectedCourse != null) {
                                CourseDetailScreen(
                                    course = selectedCourse,
                                    onEditCourse = { course ->
                                        viewModel.startEditCourse(course)
                                    },
                                    onDeleteCourse = { course ->
                                        viewModel.deleteCourse(course)
                                    },
                                    onToggleNotification = { courseId, currentStatus ->
                                        viewModel.toggleNotification(courseId, currentStatus)
                                    },
                                    onNavigateBack = {
                                        viewModel.navigateTo(AppScreen.WEEKLY_GRID)
                                    }
                                )
                            } else {
                                // Course was deleted or not found
                                LaunchedEffect(Unit) {
                                    viewModel.navigateTo(AppScreen.WEEKLY_GRID)
                                }
                            }
                        }

                        AppScreen.ADD_EDIT_COURSE -> {
                            uiState.editingCourse?.let { courseToEdit ->
                                AddEditCourseScreen(
                                    course = courseToEdit,
                                    onSaveCourse = { course ->
                                        viewModel.saveCourse(course)
                                    },
                                    onNavigateBack = {
                                        viewModel.navigateTo(
                                            if (courseToEdit.id != 0L) AppScreen.COURSE_DETAIL else AppScreen.WEEKLY_GRID
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                // Global Directions dialog if requested
                uiState.directionsCourse?.let { course ->
                    RoomDirectionsDialog(
                        course = course,
                        onDismiss = { viewModel.dismissDirections() }
                    )
                }
            }
        }
    }
}
