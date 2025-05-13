package com.example.kursach_course.api

import com.example.kursach_course.models.LoginRequest
import com.example.kursach_course.models.LoginResponse
import com.example.kursach_course.models.RegisterRequest
import com.example.kursach_course.models.RegisterResponse
import com.example.kursach_course.models.ResetCodeRequest
import com.example.kursach_course.models.SimpleResponse
import com.example.kursach_course.models.VerifyCodeRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

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
}