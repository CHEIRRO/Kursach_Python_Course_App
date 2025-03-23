package com.example.kursach_course.models

data class GlotRequest(
    val files: List<GlotFile>
)

data class GlotFile(
    val name: String,
    val content: String
)
