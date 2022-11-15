package com.ncs.quizr

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

/**
 * Override base class methods to handle any events required by the application.
 * All methods are invoked on a background thread, and may be called when the app is in the background or not open.
 *
 *  The registration token may change when:
 *  - The app deletes Instance ID
 *  - The app is restored on a new device
 *  - The user uninstalls/reinstall the app
 *  - The user clears app data.
 */


class FCMService : FirebaseMessagingService(){

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    override fun onNewToken(token:String){
        super.onNewToken(token)
        Log.i("SellerFirebaseService ","Refreshed token :: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token:String){
        // TODO : send token to tour server
    }

    override fun onMessageReceived(message: RemoteMessage){
        super.onMessageReceived(message)
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

                    //val calendar: Calendar = Calendar.getInstance()

                   // val alarmManager : AlarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                //var pendingIntent : PendingIntent = PendingIntent.getBroadcast(this,0, broadcastIntent,0)

                    //setAlarmThroughBroadCast()
                    setAlarmThroughActivity()

                }

            }

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


    }


