package com.github.Anshul1507.discountnotificationfcm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.*

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        //if message.data not empty
        if (message.data.isNotEmpty()) {
            message.data.also {
                val msg = Message(
                    it["id"],
                    it["label"],
                    it["message"],
                    it["validity"],
                    it["time"],
                    it["notif_fixed"].toBoolean(),
                    it["notif_remove_on_offer_ends"].toBoolean()
                )
                WorkerScheduler.data = msg

                Log.d("fcm data: ", msg.toString())

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val time = sdf.parse(msg.time!!)?.time!!

                scheduleAlarm(time)
            }
        }
    }

    private fun scheduleAlarm(time: Long) {
        val alarmManager: AlarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(applicationContext, AlarmBroadcastReceiver::class.java)
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)

        val alarmIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            time, alarmIntent
        )

    }

    override fun onNewToken(token: String) {
        Log.d("fcm token refreshed: ", token)
    }
}