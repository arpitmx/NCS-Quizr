package com.ncs.quizr

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import androidx.annotation.UiThread
import java.util.*
import kotlin.concurrent.schedule


class AlarmBroadcast : BroadcastReceiver() {

    companion object{
        private val TAG : String = "ALARMBROADCAST"
        lateinit var mediaPlayer : MediaPlayer

    }

    override fun onReceive(context: Context?, p1: Intent?) {
        Log.d(TAG, "Alarm request recieved")


        //we will use vibrator first
        val vibrator = context!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(4000)

        Toast.makeText(context, "Alarm! Wake up! Wake up!", Toast.LENGTH_LONG).show()

        mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.alarmtechno)
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 100, 0)
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(100f, 100f)
        mediaPlayer.start()

        Timer().schedule(20000) {
            mediaPlayer.stop()
            mediaPlayer.release()

           // Toast.makeText(context, "Now stopping alarm!", Toast.LENGTH_LONG).show()

        }

    }
}

