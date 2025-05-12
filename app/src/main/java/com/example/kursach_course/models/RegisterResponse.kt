package com.example.kursach_course.models

data class RegisterResponse(
    val userID: Int,
    val name: String,
    val email: String,
    val token: String
)