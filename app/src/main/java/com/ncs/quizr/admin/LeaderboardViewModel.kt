package com.ncs.quizr.admin

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ncs.quizr.dataClasses.QuizModelConstants
import com.ncs.quizr.dataClasses.realTimeDatabaseRefPaths

class LeaderboardViewModel : ViewModel() {

    private lateinit var db: FirebaseDatabase
    private lateinit var configRef: DatabaseReference
    private lateinit var quesRef: DatabaseReference
    val fbref: realTimeDatabaseRefPaths = realTimeDatabaseRefPaths()
    val mc: QuizModelConstants = QuizModelConstants()
    private val TAG: String = "AdminModel"


    private var bestPlayerLiveData = MutableLiveData<ArrayList<String>>()






    fun init(){
        db = Firebase.database
        configRef = db.getReference(fbref.opConfig)
        quesRef = db.getReference(fbref.quizConfig)
        setHighest()

    }


    fun getTopList() : LiveData<ArrayList<String>>{
        return bestPlayerLiveData
    }

    fun setHighest(){

        val mDatabaseHighestPlayer: Query =
            quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().correctSub).child(fbref.queStatus().submitters).orderByChild("score").limitToFirst(5)

        mDatabaseHighestPlayer.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var list = arrayListOf<String>()
                for (childSnapshot in dataSnapshot.children) {
                    val key = childSnapshot.key
                    Log.d(TAG, "onDataChange: ${key}")
                    list.add(key.toString())
                }
                bestPlayerLiveData.value = list

            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException() // don't swallow errors
            }
        })


//        quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().correctSub)
//            .addValueEventListener()
    }



}