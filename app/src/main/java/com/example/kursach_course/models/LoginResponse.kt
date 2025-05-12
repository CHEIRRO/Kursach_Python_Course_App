package com.example.kursach_course.models

data class LoginResponse(
    val token: String,
    val userID: Int,
    val name: String,
    val email: String
)