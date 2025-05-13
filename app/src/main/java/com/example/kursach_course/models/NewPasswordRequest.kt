package com.example.kursach_course.models

data class NewPasswordRequest(
    val email: String,
    val newPassword: String
)