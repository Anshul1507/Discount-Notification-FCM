package com.github.Anshul1507.discountnotificationfcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
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
        const val NOTIF_FIXED = "notif_fixed"
        const val NOTIF_REMOVE_ON_OFFER_ENDS = "notif_remove_offer_ends"

        val handler: Handler = Handler(Looper.getMainLooper())

        lateinit var data: Message
    }

    lateinit var handleRunnable: Runnable


    override fun doWork(): Result {
        val id = inputData.getString(NOTIF_ID)?.toInt()
        val label = inputData.getString(NOTIF_LABEL)
        val msg = inputData.getString(NOTIF_MSG)
        val validity = inputData.getString(NOTIF_VALIDITY)
        val isNotifFixed = inputData.getBoolean(NOTIF_FIXED, false)
        val isNotifRemoveOnOfferEnds = inputData.getBoolean(NOTIF_REMOVE_ON_OFFER_ENDS, false)

        showNotification(id!!, label, msg, validity, isNotifFixed, isNotifRemoveOnOfferEnds)

        return Result.failure()
    }

    private fun showNotification(
        ID: Int,
        label: String?,
        msg: String?,
        validity: String?,
        isNotifFixed: Boolean,
        isNotifRemoveOnOfferEnds: Boolean
    ) {
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

        val intent = Intent(applicationContext, AlarmBroadcastReceiver::class.java)
        //dismiss local notif ID => "D-1003" [if notif_id from server is 1003]
        intent.action = ("D-" + ID)

        val dismissIntent: PendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context.applicationContext, channelID)
            .setContentTitle(label)
            .setContentText(msg)
            .setSmallIcon(icon)
            .setPriority(priority)
            .setAutoCancel(true)
            .setChannelId(channelID)
            .addAction(android.R.drawable.ic_delete, "Dismiss", dismissIntent)

        if (isNotifFixed) {
            builder.setAutoCancel(false) //auto-cancel means remove notification on click on that
                .setOngoing(true) //this is the state where notification is non-swipe-able
        }

        val PROGRESS_MAX = validity?.toInt() //secs
        var PROGRESS = 0

        NotificationManagerCompat.from(context).apply {
            handleRunnable = object : Runnable {
                override fun run() {
                    if (PROGRESS == PROGRESS_MAX!!+1) {
                        // timer completes
                        builder.setContentTitle("Deal is over")
                        builder.setProgress(0, 0, false)
                        notify(ID, builder.build())

                        if(isNotifRemoveOnOfferEnds) {
                            notificationManager.cancel(ID)
                        }
                        handler.removeCallbacks(this)
                        return
                    }

                    builder.setProgress(PROGRESS_MAX, PROGRESS, false)
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