package com.example.kursach_course.models

data class VerifyCodeRequest(
    val email: String,
    val code: String
)
