package com.example.kursach_course.api

import com.example.kursach_course.models.RegisterRequest
import com.example.kursach_course.models.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface KtorApiService {
    @POST("register")
    fun register(
        @Body request: RegisterRequest
    ): Call<RegisterResponse>
}
