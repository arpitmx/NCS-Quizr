package com.ncs.quizr

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ncs.quizr.insta.InstaActivity
import com.ncs.quizr.main.MainActivity
import java.util.*


const val channelID = "alarm123"
const val channelName = "com.ncs.alarmChannel"

class FCMService : FirebaseMessagingService(){

    lateinit var pendingIntent : PendingIntent

    override fun onNewToken(token:String){
        super.onNewToken(token)
        Log.i("SellerFirebaseService ","Refreshed token :: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token:String){
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(message: RemoteMessage){
        super.onMessageReceived(message)
       // Toast.makeText(this, "Recieved Notif",Toast.LENGTH_LONG).show();
        Log.i("SellerFirebaseService ","Message from :: ${message.from}")

        if (message.notification != null){
            val title: String? = message.notification!!.title
            val body: String? = message.notification!!.body
            Log.i("SellerFirebaseService ","Message : title  ${title} \n body ${body}")

            if (message.data.size>0){
                Log.i("SellerFirebaseService ","Message : size ${message.data.size}")

                for(key in message.data.keys){
                    Log.i("SellerFirebaseService ","${key} Data : ${message.data.get(key)}")
                }

                Log.i("SellerFirebaseService ","FULL DATA : ${message.data}")

                if(message.data["ringAlarm"] == "true"){
                    Log.i("SellerFirebaseService ","Ring ring ")

                   setAlarm2()

                }

            }

        }

        if (message.notification != null) {
            generateNotification(
                message.notification!!.title!!,
                message.notification!!.body!!
            )

            setAlarm2()
        }


    }

    fun setAlarmThroughActivity(){
        val broadcastIntent = Intent(this, AlarmActivity::class.java)
        broadcastIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(broadcastIntent)
    }


    fun setAlarmThroughBroadCast(){
        val broadcastIntent = Intent(this, AlarmBroadcast::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this.applicationContext, 234324243, broadcastIntent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10 * 1000,pendingIntent)
       // Toast.makeText(this, "Alarm set in 10 seconds",Toast.LENGTH_LONG).show();

    }

    fun getRemoteView(title: String,body:String) : RemoteViews {
        val remoteView = RemoteViews("com.ncs.quizr",R.layout.auth_notification)
        remoteView.setTextViewText(R.id.title,title)
        remoteView.setTextViewText(R.id.body,body)
        remoteView.setImageViewResource(R.id.logo,R.drawable.ic_launcher_foreground)
        return remoteView
    }

    fun generateNotification(title:String, message:String){
        val intent = Intent(this, InstaActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        var pendingIntent: PendingIntent? = null
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }


        var builder : NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,
            channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setVibrate(longArrayOf(1000,1000,1000,1000))
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)

        builder = builder.setContent(getRemoteView(title, message))
        val notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelID, channelName,
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0,builder.build())

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun setAlarm2(){


        val intent = Intent(this, AlarmBroadcast::class.java)
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 * 1000,pendingIntent)


    }


    }


