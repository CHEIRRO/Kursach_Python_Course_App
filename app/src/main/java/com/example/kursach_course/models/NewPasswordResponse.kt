package com.example.kursach_course.models

data class NewPasswordResponse(
    val token: String,
    val name: String,
    val email: String
)