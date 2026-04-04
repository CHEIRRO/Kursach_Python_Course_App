// ReminderWorker.kt
package com.example.kursach_course.applications.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.kursach_course.MainActivity
import com.example.kursach_course.R

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val appContext = applicationContext
        val mainIntent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pIntent = PendingIntent.getActivity(
            appContext,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notif = NotificationCompat.Builder(appContext, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_notifications)
            .setContentTitle("Напоминание от CodeBoost")
            .setContentText("Завтра нужно пройти следующий раздел.")
            .setContentIntent(pIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(appContext)
            .notify(1001, notif)

        return Result.success()
    }
}
