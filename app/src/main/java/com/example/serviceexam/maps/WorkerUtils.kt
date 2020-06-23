package com.example.serviceexam.maps

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.serviceexam.R
import timber.log.Timber

fun makeStatusNotification(message: String, context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val name = "ServiceExam"
        val description = "Upload location"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("CHANNEL_ID", name, importance)
        channel.description = description

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    // Create the notification
    val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("ServiceExam")
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))

    // Show the notification
    NotificationManagerCompat.from(context).notify(1, builder.build())
}

/**
 * Method for sleeping for a fixed about of time to emulate slower work
 */
fun sleep() {
    try {
        Thread.sleep(3000, 0)
    } catch (e: InterruptedException) {
        Timber.e(e.message)
    }

}