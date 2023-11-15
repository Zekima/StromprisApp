package com.example.stromprisapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Service handler for håndtering av firebase cloud message (FMC) events
 * Ansvarlig for å håndtere nye FMC tokens og messages som kommer inn
 */
class MyFirebaseMessagingService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    /**
     * Blir kalt på når en ny FMC token blir opprettet
     * @param message FMC Token
     */
    @SuppressLint("LaunchActivityFromNotification")
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        super.onMessageReceived(message)
        val a = message.data["action"]
        if (a == "open_application") {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val id = 1
            val channelId = "Nye Priser"
            val channelName = message.messageType
            manager.createNotificationChannel(NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH))

            val title = message.notification?.title ?: "Default Title"
            val content = message.notification?.body ?: "Default Content"

            val intent = Intent(this, MainActivity::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            // Build the notification
            val builder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.power_icon)
                .setContentTitle(title)
                .setStyle(NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentText("Praise Jesus!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            // notificationId is a unique int for each notification that you must define
            manager.notify(id, builder.build())
        }
    }
}