package com.grup.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.grup.android.ui.MainActivity
import com.grup.device.SettingsManager
import org.koin.core.component.KoinComponent

class AndroidFirebaseMessagingService : FirebaseMessagingService(), KoinComponent {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // TODO
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(message: RemoteMessage) {
        if (allowNotification(message.data)) {
            sendNotification(message.data)
        }
    }

    private fun allowNotification(data: Map<String, String>): Boolean {
        val notificationType = data["type"] ?: return false

        return SettingsManager.AccountSettings.getGroupNotificationType(notificationType) &&
            when(notificationType) {
                SettingsManager.AccountSettings.GroupNotificationType.NewDebtAction.name ->
                    SettingsManager.LoginSettings.userId != data["userId"]
                else -> true
            }
    }

    private fun sendNotification(data: Map<String, String>) {
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                Intent(this, MainActivity::class.java),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )

        val notificationBuilder = NotificationCompat.Builder(this, "Grupit")
            .setContentTitle(data["title"])
            .setContentText(data["body"])
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.grup_logo)
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "Grupit",
                "Grupit",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Grupit Notifications"
            channel.setShowBadge(true)
            channel.canShowBadge()
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }
}