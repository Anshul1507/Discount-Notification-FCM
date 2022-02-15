package com.github.Anshul1507.discountnotificationfcm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.NOTIF_FIXED
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.NOTIF_ID
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.NOTIF_LABEL
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.NOTIF_MSG
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.NOTIF_VALIDITY
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.*

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        //if message.data not empty
        if (message.data.isNotEmpty()) {
            message.data.also {
                val data = Message(
                    it["id"],
                    it["label"],
                    it["message"],
                    it["validity"],
                    it["time"],
                    it["notif_fixed"].toBoolean()
                )
                WorkerScheduler.data = data

                Log.d("fcm data: ", data.toString())

                scheduleAlarm(data)
            }
        }
    }

    private fun scheduleAlarm(message: Message) {
        val alarmManager: AlarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(applicationContext, AlarmBroadcastReceiver::class.java)
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND)
        intent.putExtra(NOTIF_ID, message.id)
        intent.putExtra(NOTIF_LABEL, message.label)
        intent.putExtra(NOTIF_MSG, message.message)
        intent.putExtra(NOTIF_VALIDITY, message.validity)
        intent.putExtra(NOTIF_FIXED, message.isNotificationFixed)

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val time = sdf.parse(message.time!!)?.time!!

        val alarmIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
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