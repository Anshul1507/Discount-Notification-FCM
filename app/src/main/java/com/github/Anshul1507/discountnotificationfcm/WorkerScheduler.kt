package com.github.Anshul1507.discountnotificationfcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters


class WorkerScheduler(val context: Context, params: WorkerParameters) : Worker(context, params) {
    companion object {
        const val NOTIF_ID = "notif_id"
        const val NOTIF_LABEL = "notif_label"
        const val NOTIF_MSG = "notif_msg"
        const val NOTIF_VALIDITY = "notif_validity"

        lateinit var handler: Handler
    }

    lateinit var handleRunnable: Runnable


    override fun doWork(): Result {
        val id = inputData.getString(NOTIF_ID)?.toInt()
        val label = inputData.getString(NOTIF_LABEL)
        val msg = inputData.getString(NOTIF_MSG)
        val validity = inputData.getString(NOTIF_VALIDITY)

        showNotification(id!!, label, msg, validity)

        return Result.failure()
    }

    private fun showNotification(ID: Int, label: String?, msg: String?, validity: String?) {
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

        val PROGRESS_MAX = validity?.toInt() //secs
        var PROGRESS = 0

        handler = Handler(context.mainLooper)
        NotificationManagerCompat.from(context).apply {
            handleRunnable = object : Runnable {
                override fun run() {
                    if (PROGRESS == PROGRESS_MAX) {
                        // timer completes
                        builder.setContentTitle("Deal is over")
                        builder.setProgress(0, 0, false)

                        notificationManager.cancel(ID)
                        handler.removeCallbacks(this)
                        return
                    }

                    builder.setProgress(PROGRESS_MAX!!, PROGRESS, false)
                    notify(ID, builder.build())
                    PROGRESS += 1
                    builder.setContentTitle("Time left: " + (PROGRESS_MAX - PROGRESS).toString() + " secs")

                    builder.setSilent(true)
                    handler.postDelayed(this, 1000) //1 sec delay
                }

            }

            handler.post(handleRunnable)

        }

        notificationManager.notify(ID, builder.build())
    }
}