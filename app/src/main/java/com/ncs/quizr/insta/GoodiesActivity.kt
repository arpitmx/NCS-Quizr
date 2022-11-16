package com.ncs.quizr.insta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ncs.quizr.R
import com.ncs.quizr.databinding.ActivityGoodiesBinding

class GoodiesActivity : AppCompatActivity() {
    lateinit var binding: ActivityGoodiesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoodiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.button2.setOnClickListener{
            startActivity(Intent(this,InstaActivity::class.java))
        }
    }
}