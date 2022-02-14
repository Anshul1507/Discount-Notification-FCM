package com.github.Anshul1507.discountnotificationfcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class ForegroundService : Service() {
    companion object {
        const val NOTIF_ID = 1001
    }

    override fun onBind(p0: Intent?): IBinder? = null

    //this is the state called each time when service starts/called
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        startForeground(NOTIF_ID, showNotification())

        return START_NOT_STICKY
    }

    private fun showNotification(): Notification {
        val channelID = "notif_offers"
        val channelLabel = "Offers"

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            NotificationChannel(channelID, channelLabel, importance).apply {
                notificationManager.createNotificationChannel(this)
            }
        }

        val priority = Notification.PRIORITY_DEFAULT
        val icon = R.drawable.ic_launcher_background
        val title = "Notification Title"

        val builder = NotificationCompat.Builder(this)
            .setContentTitle(title)
            .setSmallIcon(icon)
            .setPriority(priority)
            .setOngoing(true)
            .setAutoCancel(false)
            .setChannelId(channelID)

        return builder.build()
    }
}