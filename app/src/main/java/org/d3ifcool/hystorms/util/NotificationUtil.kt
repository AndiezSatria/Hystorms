package org.d3ifcool.hystorms.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.ui.main.MainActivity

private const val PENGUMUMAN_ID = 1

fun createChannel(context: Context, idRes: Int, nameRes: Int, descRes: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            context.getString(idRes),
            context.getString(nameRes),
            NotificationManager.IMPORTANCE_HIGH
        )
        with(notificationChannel) {
            // Kita hanya menggunakan 1 notifikasi, jadi tidak perlu badge
            setShowBadge(false)
            enableLights(true)
            lightColor = Color.RED
            enableVibration(true)
            description = context.getString(descRes)
        }
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )
        notificationManager?.createNotificationChannel(notificationChannel)
    }
}

fun NotificationManager.sendNotification(
    context: Context,
    title: String,
    body: String
) {
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context,
        PENGUMUMAN_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT
    )
    val builder = NotificationCompat.Builder(
        context,
        "news"
    )
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setContentTitle(title)
        .setContentText(body)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
    notify(PENGUMUMAN_ID, builder.build())
}