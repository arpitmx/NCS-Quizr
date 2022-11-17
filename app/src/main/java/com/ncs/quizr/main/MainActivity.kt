package com.ncs.quizr.main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatDrawableManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.config.GservicesValue.value
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.quizr.AlarmBroadcast
import com.ncs.quizr.AlarmService
import com.ncs.quizr.dataClasses.realTimeDatabaseRefPaths
import com.ncs.quizr.dataClasses.sharedPrefsKeys
import com.ncs.quizr.databinding.ActivityMainBinding
import com.ncs.quizr.insta.GoodiesActivity
import com.ncs.quizr.quiz.QuizActivity
import java.util.*
import com.ncs.quizr.R


class MainActivity : AppCompatActivity() {



    lateinit var binding : ActivityMainBinding ;
    lateinit var pendingIntent : PendingIntent
    lateinit var sharedPref : SharedPreferences
    val pref: sharedPrefsKeys = sharedPrefsKeys()
    val fbref: realTimeDatabaseRefPaths = realTimeDatabaseRefPaths()


    private lateinit var db: FirebaseDatabase
    private lateinit var ref : DatabaseReference

    val TAG = "MainActivity"



    private val postListener = object : ValueEventListener {


        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val started = dataSnapshot.value

            if (started == fbref.opStatus().opStarted){
                Toast.makeText(applicationContext, "Quiz started by admin", Toast.LENGTH_SHORT).show()
                binding.startQuizbtn.text = "Start Quiz!\uD83C\uDF7B"
                binding.startQuizbtn.isEnabled = true
                binding.startQuizbtn.isClickable= true

                binding.startQuizbtn.startAnimation(AnimationUtils.loadAnimation(baseContext, R.anim.fade_in_slow_inf))
                binding.notice.text = "-NCS OP- \n\n Admin has started the Quiz\n tap on Start Quiz button to start...\n\n*UwU*"
                binding.startQuizbtn.background = getDrawable(baseContext,R.drawable.button_blue_curved)
                binding.progressBar.visibility = View.GONE

            }else if (started == fbref.opStatus().opNotStarted){
                Toast.makeText(applicationContext, "Wait for admin to start", Toast.LENGTH_SHORT).show()
                binding.startQuizbtn.text = "Wait for Admin!\uD83C\uDF7B "
                binding.startQuizbtn.startAnimation(AnimationUtils.loadAnimation(baseContext, R.anim.fade_in_slow_inf))
                binding.startQuizbtn.isEnabled = false
                binding.startQuizbtn.isClickable= false

                binding.notice.text = "-NCS OP- \n\n Waiting for the host to \nstart...\n\n*UwU*\n\n\n\n\n\n LOADING (👉ﾟヮﾟ)👉 ....."

                binding.startQuizbtn.background = getDrawable(baseContext,R.drawable.button_blue_curved_disabled)
                binding.progressBar.visibility = View.VISIBLE


            }else if (started == fbref.opStatus().opEnded){
                Toast.makeText(applicationContext, "OP Ended", Toast.LENGTH_SHORT).show()
                binding.startQuizbtn.text = "OP Ended! \uD83C\uDF7B "
                binding.startQuizbtn.isEnabled = false
                binding.startQuizbtn.isClickable= false

                binding.startQuizbtn.startAnimation(AnimationUtils.loadAnimation(baseContext, R.anim.fade_in_slow_inf))
                binding.notice.text = "-NCS OP- \n\n OP Ended see you at workshops! \n\n*UwU*\n\n\n\n\n\n See ya soon!"
                binding.startQuizbtn.background = getDrawable(baseContext,R.drawable.button_blue_curved_disabled)
                binding.progressBar.visibility = View.GONE


            }else {
                Toast.makeText(applicationContext, "Wait for server to respond", Toast.LENGTH_SHORT).show()
                binding.startQuizbtn.text = "Waiting for server! \uD83C\uDF7B "
                binding.startQuizbtn.isEnabled = false
                binding.startQuizbtn.isClickable= false

                binding.startQuizbtn.startAnimation(AnimationUtils.loadAnimation(baseContext, R.anim.fade_in_slow_inf))
                binding.notice.text = "-NCS OP- \n\n Waiting for the server to respond! \n\n*UwU*\n\n\n\n\n\n Hold tight!"
                binding.startQuizbtn.background = getDrawable(baseContext,R.drawable.button_blue_curved_disabled)
                binding.progressBar.visibility = View.VISIBLE


            }


            Log.d(TAG, "Value is: $started")
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Toast.makeText(applicationContext, "Low connectivity, restart." , Toast.LENGTH_SHORT).show()
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences(pref.sharedPrefID, MODE_PRIVATE)
        db = Firebase.database
        ref = db.getReference(fbref.opConfig)
        binding.progressBar.visibility = View.VISIBLE


        initViews()
        getToken()
        subscribeToTopics()




        binding.getDataBtn.setOnClickListener{
            startActivity(Intent(this, GoodiesActivity::class.java))
        }

        binding.startQuizbtn.setOnClickListener{
            startActivity(Intent(this, QuizActivity::class.java))
        }

    }

    fun initViews(){

        val anim : Animation = AnimationUtils.loadAnimation(this,com.ncs.quizr.R.anim.fade_in)
        binding.actionbr.title.startAnimation(anim)


        val options: RequestOptions = RequestOptions()
            .centerInside()
            .placeholder(com.ncs.quizr.R.drawable.goog)
            .error(com.ncs.quizr.R.drawable.goog)

        Glide.with(this).load(sharedPref.getString(pref.photoUrl,"")).apply(options).into(binding.actionbr.profileImage)
        ref.child(fbref.opConfig_isStarted).addValueEventListener(postListener)

    }

    fun subscribeToTopics(){
        FirebaseMessaging.getInstance().subscribeToTopic("alarm")
            .addOnCompleteListener { task ->
                if (task.isSuccessful()) {
                   // Toast.makeText(this, "Alarm subscribed ", Toast.LENGTH_SHORT).show()
                } else {
                  //  Toast.makeText(this, "Alarm subscription failed ", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun getToken() {

        FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
            if(result != null){
               // Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
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

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(
                ConnectivityManager
                .EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) {
                binding.startQuizbtn.isEnabled= false
                Toast.makeText(baseContext, "Low connection, please check your internet!", Toast.LENGTH_SHORT).show()
                binding.startQuizbtn.background = getDrawable(baseContext,R.drawable.button_blue_curved_disabled)

            } else {
                binding.startQuizbtn.isEnabled = true
                Toast.makeText(baseContext, "Connection stable!", Toast.LENGTH_SHORT).show()
                binding.startQuizbtn.background = getDrawable(baseContext,R.drawable.button_blue_curved)


            }
        }
    }


}

