// MainActivity.kt
package com.example.kursach_course

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.example.kursach_course.applications.notifications.NotificationHelper
import com.example.kursach_course.applications.notifications.ReminderWorker

class MainActivity : AppCompatActivity() {
    private val WORK_NAME = "reminder_work"

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        NotificationHelper.createNotificationChannel(this)
    }

    override fun onStop() {
        super.onStop()
        val prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("notif_enabled", true)
        if (enabled) {
            val request = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            WorkManager.getInstance(this)
                .enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, request)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WorkManager.getInstance(this)
            .cancelUniqueWork(WORK_NAME)
    }

    private fun applySavedTheme() {
        val sharedPref = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isDarkTheme = sharedPref.getBoolean("isDarkTheme", false)
        setTheme(if (isDarkTheme) R.style.Theme_AppDark else R.style.Theme_Kursach_course)
    }

    private fun enableEdgeToEdge() {
    }
}
