package com.example.kursach_course

import com.example.kursach_course.api.KtorApiService

class AssignmentRepository(private val api: KtorApiService) {

    suspend fun getAllPracticeAssignments(): List<AssignmentResponse> =
        api.getAllAssignments(type = "practice")

    suspend fun getAssignmentById(id: Int): AssignmentResponse =
        api.getAssignmentById(id)

    suspend fun updateProgress(email: String, assignmentId: Int, success: Boolean, code: String? = null) {
        api.updateProgress(ProgressRequest(email, assignmentId, success, code))
    }

    suspend fun getProblematicAssignments(email: String): List<ProblematicResponse> =
        api.getProblematicAssignments(email)

    suspend fun getExtraAssignment(email: String, currentAssignmentId: Int): ExtraAssignmentResponse? =
        try {
            api.getExtraAssignment(email, currentAssignmentId)
        } catch (e: Exception) {
            null
        }
}