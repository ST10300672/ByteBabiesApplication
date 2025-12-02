package com.bytebabies.app.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class Parent(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var email: String,
    var phone: String,
    var consentMedia: Boolean = false
)

data class Child(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var parentId: String,
    var teacherId: String,
    var age: Int = 0,
    var emergencyContact: String = "",
    var allergies: String = "",
    var medicalNotes: String = ""
)

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: LocalDate = LocalDate.now(),
    val location: String = ""
)

data class AttendanceRecord(
    val id: String = UUID.randomUUID().toString(),
    val childId: String,
    val date: LocalDate,
    var present: Boolean
)

data class Announcement(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long
)


data class Message(
    val id: String,
    val content: String,
    val fromParentId: String?,
    val timestamp: String, // keep as String
    val toAdmin: Boolean = false
)


data class MediaItem(
    val id: String = UUID.randomUUID().toString(),
    val uri: String,
    val uploadedBy: String,
    val childId: String? = null,
    val caption: String = ""
)

data class Meal(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val ingredients: String,
    val dietaryInfo: String
)

data class MealOrder(
    val id: String = UUID.randomUUID().toString(),
    val childId: String,
    val mealId: String,
    val date: LocalDate
)

enum class Role { ADMIN, PARENT }
