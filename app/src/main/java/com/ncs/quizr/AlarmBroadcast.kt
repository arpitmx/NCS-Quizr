package com.ncs.quizr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.util.Log
import android.widget.Toast


class AlarmBroadcast : BroadcastReceiver() {


    companion object{
        private val TAG : String = "ALARMBROADCAST"
    }

    override fun onReceive(context: Context?, p1: Intent?) {
        Log.d(TAG, "Alarm request recieved")

        Toast.makeText(context, "Broadcast alarm!", Toast.LENGTH_LONG).show()


    }
}