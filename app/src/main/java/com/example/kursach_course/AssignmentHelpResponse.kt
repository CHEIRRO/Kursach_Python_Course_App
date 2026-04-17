package com.example.kursach_course

import com.google.gson.annotations.SerializedName

data class AssignmentHelpResponse(
    @SerializedName("theoryTopics") val theoryTopics: List<Topic>,
    @SerializedName("extraAssignment") val extraAssignment: ExtraAssignmentResponse?
)