package com.ncs.quizr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.widget.Toast
import java.util.*
import kotlin.concurrent.schedule



class AlarmBroadcast : BroadcastReceiver() {


    companion object{
        private val TAG : String = "ALARMBROADCAST"
    }

    override fun onReceive(context: Context?, p1: Intent?) {
        Log.d(TAG, "Alarm request recieved")

        val i = Intent(context, AlarmActivity::class.java)
        //i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(i)

    }
}

//        mediaPlayer = MediaPlayer.create(context,R.raw.alarmtechno)
//        mediaPlayer.start()
//Toast.makeText(context, "Broadcast alarm!", Toast.LENGTH_LONG).show()

//        Timer().schedule(20000) {
//            mediaPlayer.stop()
//        }