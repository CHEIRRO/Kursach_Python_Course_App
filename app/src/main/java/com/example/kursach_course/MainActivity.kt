package com.example.kursach_course

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }

    private fun applySavedTheme() {
        val sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkTheme = sharedPref.getBoolean("isDarkTheme", false)

        if (isDarkTheme) {
            setTheme(R.style.Theme_AppDark)
        } else {
            setTheme(R.style.Theme_Kursach_course)
        }
    }
}