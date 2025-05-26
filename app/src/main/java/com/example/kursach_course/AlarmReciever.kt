package com.example.kursach_course

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.kursach_course.api.NotificationHelper

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent?) {
        val notifIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pIntent = PendingIntent.getActivity(
            context,
            0,
            notifIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_notifications)
            .setContentTitle("Напоминание от CodeBoost")
            .setContentText("Завтра нужно пройти следующий раздел.")
            .setContentIntent(pIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context)
            .notify(1001, builder.build())
    }
}