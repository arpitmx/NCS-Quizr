package com.ncs.quizr

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast


class AlarmService : Service() {
    lateinit var mp: MediaPlayer
    override fun onCreate() {
        super.onCreate()
        mp = MediaPlayer.create(applicationContext, R.raw.alarmtechno)
        this.mp.start()
        Log.d("Alarm Service","Service started")
        Toast.makeText(
            applicationContext,
            "Hi, I am service see you again after 15 minutes",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }
}