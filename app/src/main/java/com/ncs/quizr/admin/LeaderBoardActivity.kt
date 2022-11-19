package com.ncs.quizr.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.ncs.quizr.R
import com.ncs.quizr.databinding.ActivityLeaderBoardBinding

class LeaderBoardActivity : AppCompatActivity() {

    lateinit var binding : ActivityLeaderBoardBinding
    lateinit var model : LeaderboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLeaderBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = ViewModelProvider(this)[LeaderboardViewModel::class.java]
        model.init()


        model.getTopList().observe(this){ list->

            var ranklist = arrayListOf<String>()
            for (i in 0 until list.size){
                ranklist.add((i+1).toString())
            }

            var adapter = LeaderboardAdapter(this, ranklist,list)
            binding.leaderboardList.adapter = adapter

        }



    }
}