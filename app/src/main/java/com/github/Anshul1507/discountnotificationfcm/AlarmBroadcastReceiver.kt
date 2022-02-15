package com.github.Anshul1507.discountnotificationfcm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.NOTIF_ID
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.NOTIF_LABEL
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.NOTIF_MSG
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.NOTIF_VALIDITY
import androidx.core.app.NotificationManagerCompat
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.NOTIF_FIXED
import com.github.Anshul1507.discountnotificationfcm.WorkerScheduler.Companion.NOTIF_REMOVE_ON_OFFER_ENDS
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
                val id = intent.getStringExtra(NOTIF_ID)
                val label = intent.getStringExtra(NOTIF_LABEL)
                val msg = intent.getStringExtra(NOTIF_MSG)
                val validity = intent.getStringExtra(NOTIF_VALIDITY)
                val isNotifFixed = intent.getBooleanExtra(NOTIF_FIXED, false)
                val isNotifRemoveOnOfferEnds = intent.getBooleanExtra(NOTIF_REMOVE_ON_OFFER_ENDS, false)

                val notifData = Data.Builder()
                    .putString(NOTIF_ID, id)
                    .putString(NOTIF_LABEL, label)
                    .putString(NOTIF_MSG, msg)
                    .putString(NOTIF_VALIDITY, validity)
                    .putBoolean(NOTIF_FIXED, isNotifFixed)
                    .putBoolean(NOTIF_REMOVE_ON_OFFER_ENDS, isNotifRemoveOnOfferEnds)
                    .build()

                val workJob = OneTimeWorkRequest.Builder(WorkerScheduler::class.java)
                    .setInputData(notifData)
                    .build()

                WorkManager.getInstance().beginWith(workJob).enqueue()
            }
        }
    }
}