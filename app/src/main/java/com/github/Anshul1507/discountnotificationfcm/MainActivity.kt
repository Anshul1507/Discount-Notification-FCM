package com.github.Anshul1507.discountnotificationfcm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startForegroundService()
    }

    private fun startForegroundService() {
        Intent(this, ForegroundService::class.java).apply {
            startService(this)
        }
    }

    private fun stopForegroundService() {
        Intent(this, ForegroundService::class.java).apply {
            stopService(this)
        }
    }
}