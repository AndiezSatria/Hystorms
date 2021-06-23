package org.d3ifcool.hystorms.notify

import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.d3ifcool.hystorms.util.sendNotification

class FCMService : FirebaseMessagingService() {
    companion object {
        const val KEY_URL = "url"
    }

    override fun onNewToken(token: String) {
        Log.d("FCM", "Token baru: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: return
        val body = message.notification?.body ?: return
        val notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.sendNotification(
            applicationContext,
            title, body
        )
    }

}