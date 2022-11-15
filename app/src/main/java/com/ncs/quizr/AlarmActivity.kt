package com.ncs.quizr

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import com.ncs.quizr.databinding.ActivityAlarmBinding
import com.ncs.quizr.main.MainActivity
import java.util.*
import kotlin.concurrent.schedule


class AlarmActivity : AppCompatActivity() {

    companion object{
        private val TAG : String = "ALARMBROADCAST"
    }

    lateinit var binding : ActivityAlarmBinding
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Alarm request recieved")
        Toast.makeText(this, "Surprise surprise!", Toast.LENGTH_LONG).show()

        mediaPlayer = MediaPlayer.create(applicationContext,R.raw.alarmtechno)

        vibratePhone()
        ringPhone()

        Timer().schedule(20000) {
          runOnUiThread {
              Toast.makeText(
                  applicationContext,
                  "Thanks for waiting, shutting alarm!",
                  Toast.LENGTH_LONG
              ).show()
          }
            mediaPlayer.stop()
            //onBackPressed()
            startActivity(Intent(baseContext,MainActivity::class.java))
            finish()

        }




    }

    fun ringPhone(){
        mediaPlayer.start()
        Toast.makeText(this,"media playing",Toast.LENGTH_SHORT).show()

    }

    fun vibratePhone() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }
}