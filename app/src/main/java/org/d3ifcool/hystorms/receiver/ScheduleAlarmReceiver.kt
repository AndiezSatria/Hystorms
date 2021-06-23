package org.d3ifcool.hystorms.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import org.d3ifcool.hystorms.R
import org.d3ifcool.hystorms.extension.IntExt.combine
import java.util.*

class ScheduleAlarmReceiver : BroadcastReceiver() {
    companion object {
        const val EXTRA_MESSAGE = "messageSchedule"
        const val EXTRA_TITLE = "titleSchedule"
        const val EXTRA_ID = "id_schedule"

        const val INT_DEFAULT_VALUE = 102
        const val INT_CODE_ID = 10
        const val INT_ID_REPEATING = 104
    }

    override fun onReceive(context: Context, intent: Intent) {
        val message: String =
            if (intent.getStringExtra(EXTRA_MESSAGE) != null) intent.getStringExtra(
                EXTRA_MESSAGE
            )!! else ""

        val title: String =
            if (intent.getStringExtra(EXTRA_TITLE) != null) intent.getStringExtra(
                EXTRA_TITLE
            )!! else ""
        val notifId = intent.getIntExtra(
            EXTRA_ID,
            INT_DEFAULT_VALUE
        )

        showAlarmNotification(context, title, message, notifId)
    }

    fun setRepeatingTimeAlarm(
        context: Context,
        id: Int,
        startTime: Calendar,
        message: String,
        title: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ScheduleAlarmReceiver::class.java)
        val newId = INT_CODE_ID.combine(id)
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TITLE, title)
        intent.putExtra(EXTRA_ID, newId)

        val pendingIntent = PendingIntent.getBroadcast(context, newId, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
        Toast.makeText(context, "$title telah diingatkan", Toast.LENGTH_SHORT).show()
    }

    fun setDailyAlarm(
        context: Context,
        startTime: Calendar,
        message: String,
        title: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ScheduleAlarmReceiver::class.java)
        val newId = INT_ID_REPEATING
        intent.putExtra(EXTRA_MESSAGE, message)
        intent.putExtra(EXTRA_TITLE, title)
        intent.putExtra(EXTRA_ID, newId)

        val pendingIntent = PendingIntent.getBroadcast(context, newId, intent, 0)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        Toast.makeText(context, "$title telah diingatkan", Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context, id: Int, title: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ScheduleAlarmReceiver::class.java)
        val newId = INT_CODE_ID.combine(id)
        val pendingIntent = PendingIntent.getBroadcast(context, newId, intent, 0)
        pendingIntent.cancel()
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Pengingat $title dibatalkan", Toast.LENGTH_SHORT).show()
    }

    private fun showAlarmNotification(
        context: Context,
        title: String,
        message: String,
        notifId: Int
    ) {
        val channelID = "Channel_3"
        val channelName = "AlarmScheduleManager channel"

        val notificationManagerCompat =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context, android.R.color.transparent))
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setSound(alarmSound)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
            builder.setChannelId(channelID)
            notificationManagerCompat.createNotificationChannel(channel)
        }
        val notification = builder.build()
        notificationManagerCompat.notify(notifId, notification)

    }

    fun isAlarmSet(context: Context, id: Int): Boolean {
        val intent = Intent(context, ScheduleAlarmReceiver::class.java)
        val requestCode = INT_CODE_ID.combine(id)

        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null
    }
}