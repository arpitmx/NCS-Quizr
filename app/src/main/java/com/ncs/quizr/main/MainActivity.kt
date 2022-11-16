package com.ncs.quizr.main

import android.R
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.quizr.AlarmBroadcast
import com.ncs.quizr.AlarmService
import com.ncs.quizr.dataClasses.sharedPrefsKeys
import com.ncs.quizr.databinding.ActivityMainBinding
import com.ncs.quizr.insta.GoodiesActivity
import java.util.*


class MainActivity : AppCompatActivity() {



    lateinit var binding : ActivityMainBinding ;
    lateinit var pendingIntent : PendingIntent
    lateinit var sharedPref : SharedPreferences
    val pref: sharedPrefsKeys = sharedPrefsKeys()

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences(pref.sharedPrefID, MODE_PRIVATE)
        initViews()
        getToken()
        subscribeToTopics()


        val anim : Animation = AnimationUtils.loadAnimation(this,com.ncs.quizr.R.anim.fade_in)
        binding.actionbr.title.startAnimation(anim)
        //setAlarm(1)
        //setAlarm2()
        binding.getDataBtn.setOnClickListener{
            startActivity(Intent(this, GoodiesActivity::class.java))
        }

        binding.startQuizbtn.setOnClickListener{
            startActivity(Intent(this, GoodiesActivity::class.java))
        }

    }

    fun initViews(){


        val options: RequestOptions = RequestOptions()
            .centerInside()
            .placeholder(com.ncs.quizr.R.drawable.goog)
            .error(com.ncs.quizr.R.drawable.goog)

        Glide.with(this).load(sharedPref.getString(pref.photoUrl,"")).apply(options).into(binding.actionbr.profileImage)


    }

    fun subscribeToTopics(){
        FirebaseMessaging.getInstance().subscribeToTopic("alarm")
            .addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Alarm subscribed ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Alarm subscription failed ", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun getToken() {

        FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
            if(result != null){
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
                Log.d("MainActivity", "Token : $result")
                // DO your thing with your firebase token
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm(type: Int) {

        Toast.makeText(this, "Alarmmmmm", Toast.LENGTH_SHORT).show()

        // AlarmManager
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Alarm type
        val alarmType = AlarmManager.RTC_WAKEUP
        val time = Calendar.getInstance()
        time.timeInMillis = System.currentTimeMillis()
        when (type) {
            1 ->         // Set Alarm for next 20 seconds
                time.add(Calendar.SECOND, 20)
            2 ->         // Set Alarm for next 2 min
                time.add(Calendar.MINUTE, 2)
            3 ->       // Set Alarm for next 30 mins
                time.add(Calendar.MINUTE, 30)
        }
        val broadcastIntent = Intent(this, AlarmService::class.java)
        val pendingAlarmIntent = PendingIntent.getService(
            this,
            1, broadcastIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.timeInMillis, pendingAlarmIntent);
    }


    @RequiresApi(Build.VERSION_CODES.M)
    fun setAlarm2(){


        val intent = Intent(this, AlarmBroadcast::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 1000,pendingIntent)
            Toast.makeText(this, "Alarm set in 10 seconds",Toast.LENGTH_LONG).show();


    }


}