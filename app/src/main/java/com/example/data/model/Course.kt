package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Entity(tableName = "courses")
@TypeConverters(DaysConverter::class)
data class Course(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val code: String,
    val lecturer: String,
    val lecturerEmail: String = "",
    val lecturerPhone: String = "",
    val classroom: String,
    val building: String = "Main Academic Complex",
    val floor: String = "2nd Floor",
    val directions: String = "Take Central Staircase to Level 2, corridor on the left",
    val daysOfWeek: List<Int>, // 1 = Monday, 2 = Tuesday, ... 7 = Sunday
    val startHour: Int,
    val startMinute: Int,
    val endHour: Int,
    val endMinute: Int,
    val colorIndex: Int = 0,
    val recurrence: String = "Weekly", // "Weekly", "Bi-weekly", "Single Session"
    val isNotified: Boolean = false,
    val notes: String = ""
) {
    fun formatTimeRange(): String {
        val startFormatted = formatTime(startHour, startMinute)
        val endFormatted = formatTime(endHour, endMinute)
        return "$startFormatted - $endFormatted"
    }

    fun startTotalMinutes(): Int = startHour * 60 + startMinute
    fun endTotalMinutes(): Int = endHour * 60 + endMinute
    fun durationMinutes(): Int = (endTotalMinutes() - startTotalMinutes()).coerceAtLeast(15)

    fun daysString(): String {
        val names = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        return daysOfWeek.sorted().mapNotNull { dayNum ->
            if (dayNum in 1..7) names[dayNum - 1] else null
        }.joinToString(", ")
    }

    companion object {
        fun formatTime(hour: Int, minute: Int): String {
            val h = hour % 12
            val displayHour = if (h == 0) 12 else h
            val amPm = if (hour < 12) "AM" else "PM"
            return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm)
        }

        fun formatTime24(hour: Int, minute: Int): String {
            return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        }
    }
}

class DaysConverter {
    @TypeConverter
    fun fromDaysList(days: List<Int>): String {
        return days.joinToString(",")
    }

    @TypeConverter
    fun toDaysList(data: String): List<Int> {
        if (data.isBlank()) return emptyList()
        return data.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
    }
}

data class PastelCourseColor(
    val name: String,
    val backgroundHex: Long,
    val textHex: Long,
    val borderHex: Long,
    val accentHex: Long
)

object CoursePalette {
    val colors = listOf(
        PastelCourseColor("Lavender", 0xFFEDE9FE, 0xFF5B21B6, 0xFFDDD6FE, 0xFF7C3AED),
        PastelCourseColor("Sky Blue", 0xFFE0F2FE, 0xFF0369A1, 0xFFBAE6FD, 0xFF0284C7),
        PastelCourseColor("Mint Emerald", 0xFFDCFCE7, 0xFF15803D, 0xFFBBF7D0, 0xFF16A34A),
        PastelCourseColor("Warm Amber", 0xFFFEF3C7, 0xFFB45309, 0xFFFDE68A, 0xFFD97706),
        PastelCourseColor("Soft Rose", 0xFFFFE4E6, 0xFFBE123C, 0xFFFECDD3, 0xFFE11D48),
        PastelCourseColor("Indigo", 0xFFE0E7FF, 0xFF4338CA, 0xFFC7D2FE, 0xFF4F46E5),
        PastelCourseColor("Teal Sage", 0xFFCCFBF1, 0xFF0F766E, 0xFF99F6E4, 0xFF0D9488),
        PastelCourseColor("Peach Tangerine", 0xFFFFEDD5, 0xFFC2410C, 0xFFFED7AA, 0xFFEA580C)
    )

    fun getColor(index: Int): PastelCourseColor {
        val safeIndex = index.mod(colors.size)
        return colors[safeIndex]
    }
}
