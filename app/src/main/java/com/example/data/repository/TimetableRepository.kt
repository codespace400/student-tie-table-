package com.example.data.repository

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.CourseDao
import com.example.data.model.Course
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettings(
    val reminderLeadTime: Int = 10, // minutes (5, 10, 15, 30)
    val weekStartDay: Int = 1, // 1 = Monday, 7 = Sunday
    val themeMode: String = "SYSTEM" // "SYSTEM", "LIGHT", "DARK"
)

class TimetableRepository(
    private val courseDao: CourseDao,
    private val context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("classmate_prefs", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow = _settingsFlow.asStateFlow()

    private fun loadSettings(): UserSettings {
        val leadTime = sharedPrefs.getInt("reminder_lead_time", 10)
        val weekStart = sharedPrefs.getInt("week_start_day", 1)
        val theme = sharedPrefs.getString("theme_mode", "SYSTEM") ?: "SYSTEM"
        return UserSettings(
            reminderLeadTime = leadTime,
            weekStartDay = weekStart,
            themeMode = theme
        )
    }

    fun updateReminderLeadTime(minutes: Int) {
        sharedPrefs.edit().putInt("reminder_lead_time", minutes).apply()
        _settingsFlow.value = _settingsFlow.value.copy(reminderLeadTime = minutes)
    }

    fun updateWeekStartDay(startDay: Int) {
        sharedPrefs.edit().putInt("week_start_day", startDay).apply()
        _settingsFlow.value = _settingsFlow.value.copy(weekStartDay = startDay)
    }

    fun updateThemeMode(theme: String) {
        sharedPrefs.edit().putString("theme_mode", theme).apply()
        _settingsFlow.value = _settingsFlow.value.copy(themeMode = theme)
    }

    fun getAllCourses(): Flow<List<Course>> = courseDao.getAllCourses()

    fun getCourseById(id: Long): Flow<Course?> = courseDao.getCourseById(id)

    suspend fun getCourseByIdImmediate(id: Long): Course? = courseDao.getCourseByIdImmediate(id)

    suspend fun insertCourse(course: Course): Long = courseDao.insertCourse(course)

    suspend fun updateCourse(course: Course) = courseDao.updateCourse(course)

    suspend fun deleteCourse(course: Course) = courseDao.deleteCourse(course)

    suspend fun deleteCourseById(id: Long) = courseDao.deleteCourseById(id)

    suspend fun toggleNotification(courseId: Long, isNotified: Boolean) {
        courseDao.updateNotificationStatus(courseId, isNotified)
    }

    suspend fun resetToSampleData() {
        courseDao.deleteAllCourses()
        courseDao.insertAll(AppDatabase.getSampleCourses())
    }

    suspend fun clearAllData() {
        courseDao.deleteAllCourses()
    }
}
