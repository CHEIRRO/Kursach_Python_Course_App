package com.example.kursach_course.api

import com.example.kursach_course.models.GlotRequest
import com.example.kursach_course.models.GlotResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiServiceforWriteCode {
    @POST("run/python/latest")
    fun runPythonCode(
        @Header("Authorization") token: String,
        @Body request: GlotRequest
    ): Call<GlotResponse>
}

