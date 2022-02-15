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


class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val id = intent.getStringExtra(NOTIF_ID)
            val label = intent.getStringExtra(NOTIF_LABEL)
            val msg = intent.getStringExtra(NOTIF_MSG)
            val validity = intent.getStringExtra(NOTIF_VALIDITY)

            val notifData = Data.Builder()
                .putString(NOTIF_ID, id)
                .putString(NOTIF_LABEL, label)
                .putString(NOTIF_MSG, msg)
                .putString(NOTIF_VALIDITY, validity)
                .build()

            val workJob = OneTimeWorkRequest.Builder(WorkerScheduler::class.java)
                .setInputData(notifData)
                .build()

            WorkManager.getInstance().beginWith(workJob).enqueue()
        }
    }
}