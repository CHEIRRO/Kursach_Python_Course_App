package com.example.kursach_course.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

interface RestoreApi {
    @POST("/restore/send")
    suspend fun sendResetEmail(@Body request: EmailRequest): Response<String>
}

data class EmailRequest(val email: String)

object RetrofitInstance {
    val api: RestoreApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080") // локальный хост для эмулятора Android
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(RestoreApi::class.java)
    }
}
