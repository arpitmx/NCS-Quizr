package com.ncs.quizr.main

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ncs.quizr.databinding.ActivityMainBinding
import com.ncs.quizr.insta.InstaActivity
import kotlin.math.log

class MainActivity : AppCompatActivity() {



    lateinit var binding : ActivityMainBinding ;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getToken()
        subscribeToTopics()

        binding.getDataBtn.setOnClickListener{
            startActivity(Intent(this, InstaActivity::class.java))
        }

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

}