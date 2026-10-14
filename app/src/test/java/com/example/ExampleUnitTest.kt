package com.example

import com.example.data.model.Course
import com.example.data.model.CoursePalette
import com.example.data.model.DaysConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun course_timeFormatting_isCorrect() {
        val course = Course(
            name = "Data Structures",
            code = "CS 201",
            lecturer = "Dr. Alan Turing",
            classroom = "Turing Lab 302",
            daysOfWeek = listOf(1, 3),
            startHour = 9,
            startMinute = 0,
            endHour = 10,
            endMinute = 30
        )
        assertEquals("9:00 AM - 10:30 AM", course.formatTimeRange())
        assertEquals(90, course.durationMinutes())
        assertEquals("Mon, Wed", course.daysString())
    }

    @Test
    fun daysConverter_serializesAndDeserializes() {
        val converter = DaysConverter()
        val days = listOf(1, 3, 5)
        val serialized = converter.fromDaysList(days)
        assertEquals("1,3,5", serialized)

        val deserialized = converter.toDaysList(serialized)
        assertEquals(days, deserialized)
    }

    @Test
    fun coursePalette_returnsDistinctPastels() {
        assertEquals(8, CoursePalette.colors.size)
        val color0 = CoursePalette.getColor(0)
        assertEquals("Lavender", color0.name)
        val colorSafe = CoursePalette.getColor(8)
        assertEquals(color0.name, colorSafe.name)
    }
}
