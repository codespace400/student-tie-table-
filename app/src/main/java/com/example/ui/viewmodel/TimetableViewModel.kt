package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.Course
import com.example.data.repository.TimetableRepository
import com.example.data.repository.UserSettings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

enum class AppScreen {
    WEEKLY_GRID,
    DAILY_VIEW,
    LECTURER_LIST,
    SETTINGS,
    COURSE_DETAIL,
    ADD_EDIT_COURSE
}

data class TimetableUiState(
    val courses: List<Course> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val selectedDayOfWeek: Int = 1, // 1 = Mon .. 7 = Sun
    val selectedDate: LocalDate = LocalDate.now(),
    val currentScreen: AppScreen = AppScreen.WEEKLY_GRID,
    val selectedCourseId: Long? = null,
    val editingCourse: Course? = null,
    val directionsCourse: Course? = null,
    val lecturerSearchQuery: String = "",
    val snackbarMessage: String? = null
)

class TimetableViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TimetableRepository

    private val _uiState = MutableStateFlow(
        TimetableUiState(
            selectedDayOfWeek = currentDayOfWeekNum(),
            selectedDate = LocalDate.now()
        )
    )
    val uiState: StateFlow<TimetableUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = TimetableRepository(database.courseDao(), application)

        viewModelScope.launch {
            repository.settingsFlow.collect { settings ->
                _uiState.value = _uiState.value.copy(settings = settings)
            }
        }

        viewModelScope.launch {
            repository.getAllCourses().collect { courses ->
                _uiState.value = _uiState.value.copy(courses = courses)
            }
        }
    }

    private fun currentDayOfWeekNum(): Int {
        val day = LocalDate.now().dayOfWeek.value // 1 = Mon .. 7 = Sun
        return day
    }

    fun navigateTo(screen: AppScreen) {
        _uiState.value = _uiState.value.copy(currentScreen = screen)
    }

    fun selectDay(dayOfWeek: Int) {
        _uiState.value = _uiState.value.copy(selectedDayOfWeek = dayOfWeek)
    }

    fun jumpToDayInDailyView(dayOfWeek: Int) {
        _uiState.value = _uiState.value.copy(
            selectedDayOfWeek = dayOfWeek,
            currentScreen = AppScreen.DAILY_VIEW
        )
    }

    fun viewCourseDetail(courseId: Long) {
        _uiState.value = _uiState.value.copy(
            selectedCourseId = courseId,
            currentScreen = AppScreen.COURSE_DETAIL
        )
    }

    fun startAddCourse(presetDay: Int? = null) {
        val defaultDay = presetDay ?: _uiState.value.selectedDayOfWeek
        val newCourse = Course(
            name = "",
            code = "",
            lecturer = "",
            classroom = "",
            daysOfWeek = listOf(defaultDay),
            startHour = 9,
            startMinute = 0,
            endHour = 10,
            endMinute = 30,
            colorIndex = (_uiState.value.courses.size) % 8,
            recurrence = "Weekly"
        )
        _uiState.value = _uiState.value.copy(
            editingCourse = newCourse,
            currentScreen = AppScreen.ADD_EDIT_COURSE
        )
    }

    fun startEditCourse(course: Course) {
        _uiState.value = _uiState.value.copy(
            editingCourse = course,
            currentScreen = AppScreen.ADD_EDIT_COURSE
        )
    }

    fun saveCourse(course: Course) {
        viewModelScope.launch {
            if (course.id == 0L) {
                val newId = repository.insertCourse(course)
                showSnackbar("Course added: ${course.code}")
            } else {
                repository.updateCourse(course)
                showSnackbar("Course updated: ${course.code}")
            }
            _uiState.value = _uiState.value.copy(
                editingCourse = null,
                selectedCourseId = if (course.id != 0L) course.id else null,
                currentScreen = if (course.id != 0L) AppScreen.COURSE_DETAIL else AppScreen.WEEKLY_GRID
            )
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.deleteCourse(course)
            showSnackbar("Course deleted: ${course.code}")
            _uiState.value = _uiState.value.copy(
                selectedCourseId = null,
                editingCourse = null,
                currentScreen = AppScreen.WEEKLY_GRID
            )
        }
    }

    fun toggleNotification(courseId: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            val newStatus = !currentStatus
            repository.toggleNotification(courseId, newStatus)
            val leadTime = _uiState.value.settings.reminderLeadTime
            if (newStatus) {
                showSnackbar("Reminder enabled ($leadTime min before class)")
            } else {
                showSnackbar("Reminder disabled")
            }
        }
    }

    fun showDirections(course: Course?) {
        _uiState.value = _uiState.value.copy(directionsCourse = course)
    }

    fun dismissDirections() {
        _uiState.value = _uiState.value.copy(directionsCourse = null)
    }

    fun setLecturerSearch(query: String) {
        _uiState.value = _uiState.value.copy(lecturerSearchQuery = query)
    }

    fun updateReminderLeadTime(minutes: Int) {
        repository.updateReminderLeadTime(minutes)
        showSnackbar("Reminder lead-time updated to $minutes minutes")
    }

    fun updateWeekStartDay(startDay: Int) {
        repository.updateWeekStartDay(startDay)
        val dayName = if (startDay == 1) "Monday" else "Sunday"
        showSnackbar("Week start day set to $dayName")
    }

    fun updateThemeMode(theme: String) {
        repository.updateThemeMode(theme)
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.resetToSampleData()
            showSnackbar("Reset timetable to sample courses")
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            showSnackbar("All courses cleared")
        }
    }

    fun showSnackbar(message: String) {
        _uiState.value = _uiState.value.copy(snackbarMessage = message)
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}
