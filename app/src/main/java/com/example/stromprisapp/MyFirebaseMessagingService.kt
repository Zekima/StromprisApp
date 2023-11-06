package com.example.stromprisapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        println("12")
        super.onMessageReceived(message)
        val action = message.data["action"]
         if (action == "open_application") {

             println(13)

             val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
             val id = 1
             val channelId = "Nye Priser"
             val channelName = message.messageType
             manager.createNotificationChannel(NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH))

             val bText = "dddd"

             val bIntent = Intent(this, MainActivity::class.java)
             val bPendingIntent: PendingIntent =
                 PendingIntent.getBroadcast(this, 0, bIntent, PendingIntent.FLAG_IMMUTABLE)

             val intent = Intent(this, MainActivity::class.java)
             val pendingIntent = PendingIntent.getActivity(this,
                 1, intent, PendingIntent.FLAG_IMMUTABLE)

             val not = NotificationCompat.Builder(this, channelId)
                 .setSmallIcon(R.drawable.ic_launcher_background)
                 .setContentTitle(message.notification?.title)
                 .setContentText(message.notification?.body)
                 .setContentIntent(pendingIntent)
                 .setAutoCancel(true)
                 .addAction(com.google.firebase.messaging.ktx.R.drawable.common_google_signin_btn_icon_dark_focused,bText, bPendingIntent)
                 .build()
             manager.notify(id, not)
         }


    }

}