package com.ncs.quizr.admin

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.ncs.quizr.dataClasses.QuizModelConstants
import com.ncs.quizr.dataClasses.leaderboardUser
import com.ncs.quizr.dataClasses.realTimeDatabaseRefPaths
import kotlin.math.log


class AdminActivityViewModel : ViewModel() {

    private lateinit var db: FirebaseDatabase
    private lateinit var configRef: DatabaseReference
    private lateinit var quesRef: DatabaseReference
    val fbref: realTimeDatabaseRefPaths = realTimeDatabaseRefPaths()
    val mc: QuizModelConstants = QuizModelConstants()
    private val TAG: String = "AdminModel"

    private var quizStatusResponseCode = MutableLiveData<Int>()
    private var currentQuestionIndex = MutableLiveData<Int>()
    private var opStatus = MutableLiveData<String>()
    private var isQueSetLiveData = MutableLiveData<Int>()
    private var bestPlayerLiveData = MutableLiveData<String>()
    private var totalSubsLiveData = MutableLiveData<String>()
    private var winnerDetailLiveData = MutableLiveData<ArrayList<String>>()


    private val statusListner = object : ValueEventListener {


        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val started = dataSnapshot.value

            if (started == fbref.opStatus().opStarted) {
                opStatus.value = "Running"
            } else if (started == fbref.opStatus().opNotStarted) {
                opStatus.value = "Stopped"
            } else if (started == fbref.opStatus().opEnded) {
                opStatus.value = "Ended"
            } else {
                opStatus.value = "ERR STATUS"
            }
            Log.d(TAG, "Value is: $started")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d(TAG, "Value is: Error")

        }


    }

    fun init(){
        db = Firebase.database
        configRef = db.getReference(fbref.opConfig)
        quesRef = db.getReference(fbref.quizConfig)

        configRef.child(fbref.opConfig_isStarted).addValueEventListener(statusListner)

    }

    fun setQuizStatus( statusCode: String){
                    configRef.child(fbref.opConfig_isStarted).setValue(statusCode)
                        .addOnCompleteListener {
                            if (statusCode==fbref.opStatus().opStarted){
                                quizStatusResponseCode.value = 200
                            }else if (statusCode==fbref.opStatus().opEnded){
                                quizStatusResponseCode.value = 203
                            }else {
                                quizStatusResponseCode.value = 202
                            }
                       }
                        .addOnFailureListener {
                            quizStatusResponseCode.value = 404
                        }
    }

    private val totalSubmissionListner = object : ValueEventListener {


        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val totalSubmisisons = dataSnapshot.value
            totalSubsLiveData.value = totalSubmisisons.toString()

        }

        override fun onCancelled(error: DatabaseError) {
            Log.d(TAG, "Value is: Error")

        }


    }

    fun getTotalSubs(){
        quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().totalSub).addValueEventListener(totalSubmissionListner)
    }
    fun getTotalSubLiveData():LiveData<String>{
        return totalSubsLiveData
    }

    fun getWinner() : LiveData<ArrayList<String>>{
        return winnerDetailLiveData
    }

    fun setHighest(){

        val mDatabaseHighestPlayer: Query =
            quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().correctSub).child(fbref.queStatus().submitters).orderByChild("score").limitToFirst(1)

        mDatabaseHighestPlayer.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val key = childSnapshot.key
                    val email = childSnapshot.child("email").value
                    winnerDetailLiveData.value = arrayListOf(key.toString(),email.toString())
                    bestPlayerLiveData.value = childSnapshot.key.toString()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                throw databaseError.toException() // don't swallow errors
            }
        })


//        quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().correctSub)
//            .addValueEventListener()
    }

    fun setQuestion(index: String){
        quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().index)
            .setValue(index)
            .addOnCompleteListener{
                isQueSetLiveData.value = 200
            }.addOnFailureListener {
                isQueSetLiveData.value = 404
            }

        resetValues()
    }

    fun resetValues(){

        //Total submissions = 0
        quesRef.child(fbref.queStatus().currentQue)
            .child(fbref.queStatus().totalSub).setValue(0)
        // Winner = reset
        quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().winner).
            child("email").setValue("")
        quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().winner).
            child("username").setValue("")
        // Correct subs to reset
        quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().correctSub)
            .removeValue()





    }

    fun getStatusResponse(): LiveData<String> {
        return opStatus
    }
    fun getIsQueSetAtIndex(): LiveData<Int> {
        return isQueSetLiveData
    }

    fun getHighestPlayer(): LiveData<String> {
        return bestPlayerLiveData
    }

    fun postWinner(username: String, email: String) {

        quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().winner).
        child("email").setValue(email)
        quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().winner).
        child("username").setValue(username)


    }

    fun setQuesStatus(status:Int) {
        if (status == 1){
        quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().questionStatus)
        .setValue(fbref.queStatus().counting)
        }else if (status == 0){
            quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().questionStatus)
                .setValue(fbref.queStatus().over)
        }

    }

}