package com.example.kursach_course.api

import com.example.kursach_course.Assignment
import com.example.kursach_course.AssignmentHelpResponse
import com.example.kursach_course.AssignmentResponse
import com.example.kursach_course.ExtraAssignmentResponse
import com.example.kursach_course.ProblematicResponse
import com.example.kursach_course.ProgressRequest
import com.example.kursach_course.SubtopicResponse
import com.example.kursach_course.Topic
import com.example.kursach_course.TopicWithPracticeResponse
import com.example.kursach_course.models.LoginRequest
import com.example.kursach_course.models.LoginResponse
import com.example.kursach_course.models.NewPasswordRequest
import com.example.kursach_course.models.NewPasswordResponse
import com.example.kursach_course.models.RegisterRequest
import com.example.kursach_course.models.RegisterResponse
import com.example.kursach_course.models.ResetCodeRequest
import com.example.kursach_course.models.VerifyCodeRequest
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface KtorApiService {
    @POST("register")
    fun register(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>

    @POST("login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>

    @POST("restore/send")
    fun requestResetCode(
        @Body request:ResetCodeRequest
    ): Call<Void>

    @POST("restore/verify")
    fun verifyResetCode(
        @Body request: VerifyCodeRequest
    ): Call<Void>

    @POST("restore/reset")
    fun confirmPasswordReset(
        @Body request: NewPasswordRequest
    ): Call<NewPasswordResponse>

    @GET("assignments")
    suspend fun getAllAssignments(@Query("type") type: String? = null): List<AssignmentResponse>

    @GET("assignment/{id}")
    suspend fun getAssignmentById(@Path("id") id: Int): AssignmentResponse

    @POST("progress")
    suspend fun updateProgress(@Body request: ProgressRequest): Response<Unit>

    @GET("problematic")
    suspend fun getProblematicAssignments(@Query("email") email: String): List<ProblematicResponse>

    @GET("problematic/extra")
    suspend fun getExtraAssignment(
        @Query("email") email: String,
        @Query("assignmentId") assignmentId: Int
    ): ExtraAssignmentResponse

    @GET("topics/practice")
    suspend fun getTopicsWithPractice(): List<TopicWithPracticeResponse>

    @GET("topics")
    fun getTheoryTopics(): Call<List<Topic>>

    @GET("topics/{topicId}/assignments")
    fun getTheoryAssignments(@Path("topicId") topicId: Int): Call<List<Assignment>>

    @GET("topics")
    suspend fun getTheoryTopicsSuspend(): List<Topic>

    @GET("topics/{topicId}/subtopics")
    suspend fun getSubtopicsByTopic(
        @Path("topicId") topicId: Int,
        @Query("email") email: String?
    ): List<SubtopicResponse>

    @GET("subtopics/{subtopicId}/assignment")
    suspend fun getAssignmentBySubtopic(
        @Path("subtopicId") subtopicId: Int
    ): AssignmentResponse?

    @POST("subtopics/{subtopicId}/complete")
    suspend fun completeSubtopic(
        @Path("subtopicId") subtopicId: Int,
        @Query("email") email: String
    ): Map<String, String>
    @GET("assignments/{assignmentId}/help")
    suspend fun getHelpForAssignment(
        @Path("assignmentId") assignmentId: Int
    ): AssignmentHelpResponse
    @GET("extra-assignments/{extraId}")
    suspend fun getExtraAssignmentById(
        @Path("extraId") extraId: Int
    ): ExtraAssignmentResponse

}