package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Course
import com.example.data.model.DaysConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Course::class], version = 1, exportSchema = false)
@TypeConverters(DaysConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "classmate_timetable.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        fun getSampleCourses(): List<Course> {
            return listOf(
                Course(
                    name = "Data Structures & Algorithms",
                    code = "CS 201",
                    lecturer = "Dr. Alan Turing",
                    lecturerEmail = "a.turing@university.edu",
                    lecturerPhone = "+1 (555) 349-2010",
                    classroom = "Turing Lab 302",
                    building = "Ada Lovelace Computing Center",
                    floor = "3rd Floor",
                    directions = "North elevator to 3rd floor, turn right past the robotics lab.",
                    daysOfWeek = listOf(1, 3), // Mon, Wed
                    startHour = 9,
                    startMinute = 0,
                    endHour = 10,
                    endMinute = 30,
                    colorIndex = 0, // Lavender
                    recurrence = "Weekly",
                    isNotified = true,
                    notes = "Midterm project in week 8. Bring laptop with IDE."
                ),
                Course(
                    name = "Linear Algebra & Matrices",
                    code = "MATH 152",
                    lecturer = "Prof. Katherine Johnson",
                    lecturerEmail = "k.johnson@university.edu",
                    lecturerPhone = "+1 (555) 782-9014",
                    classroom = "Lecture Hall 105",
                    building = "Euler Mathematical Pavilion",
                    floor = "1st Floor",
                    directions = "Main foyer entrance, lecture hall on immediate left.",
                    daysOfWeek = listOf(2, 4), // Tue, Thu
                    startHour = 10,
                    startMinute = 0,
                    endHour = 11,
                    endMinute = 30,
                    colorIndex = 3, // Warm Amber
                    recurrence = "Weekly",
                    isNotified = false,
                    notes = "Weekly problem sets due every Thursday at midnight."
                ),
                Course(
                    name = "General Physics & Mechanics",
                    code = "PHY 110",
                    lecturer = "Dr. Richard Feynman",
                    lecturerEmail = "r.feynman@university.edu",
                    lecturerPhone = "+1 (555) 819-4501",
                    classroom = "Amphitheater A",
                    building = "Newton Physical Sciences Hall",
                    floor = "Ground Floor",
                    directions = "Courtyard path, glass doors to Amphitheater A.",
                    daysOfWeek = listOf(3, 5), // Wed, Fri
                    startHour = 11,
                    startMinute = 0,
                    endHour = 12,
                    endMinute = 30,
                    colorIndex = 2, // Mint Emerald
                    recurrence = "Weekly",
                    isNotified = true,
                    notes = "Lab coats required for Wednesday lab practicals."
                ),
                Course(
                    name = "Interaction & UI/UX Design",
                    code = "DES 310",
                    lecturer = "Prof. Don Norman",
                    lecturerEmail = "d.norman@university.edu",
                    lecturerPhone = "+1 (555) 234-8765",
                    classroom = "Studio 4B",
                    building = "Bauhaus Arts & Media Center",
                    floor = "4th Floor",
                    directions = "South glass elevator to Level 4, open design loft.",
                    daysOfWeek = listOf(1, 5), // Mon, Fri
                    startHour = 13,
                    startMinute = 0,
                    endHour = 15,
                    endMinute = 0,
                    colorIndex = 1, // Sky Blue
                    recurrence = "Weekly",
                    isNotified = true,
                    notes = "Figma prototypes review every alternate Friday."
                ),
                Course(
                    name = "Technical Communication",
                    code = "ENG 205",
                    lecturer = "Dr. Maya Angelou",
                    lecturerEmail = "m.angelou@university.edu",
                    lecturerPhone = "+1 (555) 678-1290",
                    classroom = "Seminar Room 204",
                    building = "Humanities Quadrangle",
                    floor = "2nd Floor",
                    directions = "West brick staircase to 2nd floor, beside faculty library.",
                    daysOfWeek = listOf(2, 4), // Tue, Thu
                    startHour = 14,
                    startMinute = 0,
                    endHour = 15,
                    endMinute = 30,
                    colorIndex = 4, // Soft Rose
                    recurrence = "Weekly",
                    isNotified = false,
                    notes = "Oral presentation schedule posted on campus portal."
                ),
                Course(
                    name = "Database Systems & Cloud",
                    code = "CS 340",
                    lecturer = "Dr. Edgar Codd",
                    lecturerEmail = "e.codd@university.edu",
                    lecturerPhone = "+1 (555) 902-3341",
                    classroom = "Tech Lab 401",
                    building = "Ada Lovelace Computing Center",
                    floor = "4th Floor",
                    directions = "North elevator to 4th floor, end of east corridor.",
                    daysOfWeek = listOf(4), // Thu
                    startHour = 16,
                    startMinute = 0,
                    endHour = 18,
                    endMinute = 0,
                    colorIndex = 5, // Indigo
                    recurrence = "Weekly",
                    isNotified = false,
                    notes = "SQL schema design lab session."
                )
            )
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val courseDao = database.courseDao()
                    courseDao.insertAll(getSampleCourses())
                }
            }
        }
    }
}
