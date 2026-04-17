package com.example.kursach_course

import com.google.gson.annotations.SerializedName

data class SubtopicResponse(
    @SerializedName("subtopicId") val subtopicId: Int,
    @SerializedName("topicId") val topicId: Int,
    @SerializedName("orderIndex") val orderIndex: Int,
    @SerializedName("title") val title: String,
    @SerializedName("isCompleted") val isCompleted: Boolean = false
)