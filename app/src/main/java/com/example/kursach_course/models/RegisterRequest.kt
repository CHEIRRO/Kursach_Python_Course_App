package com.example.kursach_course.models

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)