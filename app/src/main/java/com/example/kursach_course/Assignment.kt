package com.example.kursach_course

data class Assignment(
    val assignmentId: Int,
    val title: String,
    val content: String,
    val correctAnswer: String? = null
)