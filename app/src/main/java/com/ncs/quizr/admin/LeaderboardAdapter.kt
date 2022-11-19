package com.ncs.quizr.admin

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ncs.quizr.R

class LeaderboardAdapter (private val context: Activity, private val title: ArrayList<String>, private val description: ArrayList<String>)
        : ArrayAdapter<String>(context,R.layout.activity_leader_board, title) {

        override fun getView(position: Int, view: View?, parent: ViewGroup): View {
            val inflater = context.layoutInflater
            val rowView = inflater.inflate(R.layout.item, null, true)

            val tposition = rowView.findViewById(R.id.position) as TextView
            val tusername = rowView.findViewById(R.id.username) as TextView

            tposition.text = title.get(position)
            tusername.text = description.get(position)

            return rowView
        }
    }
