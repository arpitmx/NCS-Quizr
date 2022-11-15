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

        //context!!.getSystemService(Context.VIBRATOR_SERVICE).vibrate(4000)

        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show()
        var alarmUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        }

        // setting default ringtone

        // setting default ringtone
        val ringtone = RingtoneManager.getRingtone(context, alarmUri)

        // play ringtone

        // play ringtone
        ringtone.play()

    }
}