package com.github.Anshul1507.discountnotificationfcm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.handler


class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if (intent.action.toString()[0] == 'D') {
                //Dismiss notification based on their IDs
                handler.removeCallbacksAndMessages(null)
                val notifDismissId = intent.action.toString().removeRange(0..1)
                    .toInt() //extracting notif_id from "D-1003"
                NotificationManagerCompat.from(context!!.applicationContext)
                    .cancel(null, notifDismissId);
            } else {
                val workJob = OneTimeWorkRequest.Builder(WorkerScheduler::class.java)
                    .build()

                WorkManager.getInstance().beginWith(workJob).enqueue()
            }
        }
    }
}