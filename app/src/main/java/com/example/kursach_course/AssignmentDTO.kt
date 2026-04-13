package com.example.kursach_course

// data/AssignmentDTO.kt
data class AssignmentResponse(
    val assignmentId: Int,
    val topicId: Int,
    val type: String,
    val title: String,
    val content: String,
    val correctAnswer: String? = null
)

data class ProgressRequest(
    val email: String,
    val assignmentId: Int,
    val success: Boolean,
    val code: String? = null
)

data class ProblematicResponse(
    val assignmentId: Int,
    val title: String,
    val topicTitle: String,
    val attemptCount: Int
)

data class ExtraAssignmentResponse(
    val assignmentId: Int,
    val title: String,
    val content: String,
    val correctAnswer: String?
)