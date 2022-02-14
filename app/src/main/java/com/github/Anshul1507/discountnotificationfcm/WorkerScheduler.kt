package com.github.Anshul1507.discountnotificationfcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlin.random.Random

class WorkerScheduler (val context: Context, params: WorkerParameters): Worker(context, params){
    companion object {
        const val NOTIF_LABEL = "notif_label"
        const val NOTIF_MSG = "notif_msg"
        const val NOTIF_VALIDITY = "notif_validity"
    }

    override fun doWork(): Result {
        val label = inputData.getString(NOTIF_LABEL)
        val msg = inputData.getString(NOTIF_MSG)
        val validity = inputData.getString(NOTIF_VALIDITY)

        showNotification(label, msg, validity)

        return Result.success()
    }

    private fun showNotification(label: String?, msg: String?, validity: String?) {
        val channelID = "notif_offers"
        val channelLabel = "Offers"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            NotificationChannel(channelID, channelLabel, importance).apply {
                notificationManager.createNotificationChannel(this)
            }
        }

        val priority = NotificationCompat.PRIORITY_HIGH
        val icon = R.drawable.ic_launcher_background

        val builder = NotificationCompat.Builder(context.applicationContext, channelID)
            .setContentTitle(label)
            .setContentText(msg)
            .setSmallIcon(icon)
            .setPriority(priority)
            .setAutoCancel(true)
            .setChannelId(channelID)

        notificationManager.notify(Random.nextInt(), builder.build())
    }
}