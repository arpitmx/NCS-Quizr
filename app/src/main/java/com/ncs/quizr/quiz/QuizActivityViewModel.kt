package com.ncs.quizr.quiz

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.ncs.quizr.R
import com.ncs.quizr.dataClasses.Question
import com.ncs.quizr.dataClasses.Questions
import com.ncs.quizr.dataClasses.QuizModelConstants
import com.ncs.quizr.dataClasses.realTimeDatabaseRefPaths

class QuizActivityViewModel : ViewModel() {

    private lateinit var db: FirebaseDatabase
    private lateinit var configRef: DatabaseReference
    private lateinit var quesRef: DatabaseReference
    val fbref: realTimeDatabaseRefPaths = realTimeDatabaseRefPaths()
    val mc: QuizModelConstants = QuizModelConstants()
    private val TAG: String = "QuizModel"

    private var quizStatusResponseCode = MutableLiveData<Int>()
    private var currentQuestionIndex = MutableLiveData<Int>()

    private val validityListner = object : ValueEventListener {


        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val started = dataSnapshot.value

            if (started == fbref.opStatus().opStarted) {
                quizStatusResponseCode.value = mc.Validity().running
            } else if (started == fbref.opStatus().opNotStarted) {
                quizStatusResponseCode.value = mc.Validity().notStarted
            } else if (started == fbref.opStatus().opEnded) {
                quizStatusResponseCode.value = mc.Validity().finished
            } else {
                quizStatusResponseCode.value = mc.Validity().error
            }
            Log.d(TAG, "Value is: $started")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d(TAG, "Value is: Error")

        }


    }
    private val currentQuizQueListner= object : ValueEventListener {


        override fun onDataChange(dataSnapshot: DataSnapshot) {

            val index = dataSnapshot.getValue<String>()
            currentQuestionIndex.value = Integer.parseInt(index!!)
        }
        override fun onCancelled(error: DatabaseError) {
            Log.d(TAG, "Error: ${error}")
            currentQuestionIndex.value = -404
        }
    }


    fun initModel() {
        db = Firebase.database
        configRef = db.getReference(fbref.opConfig)
        quesRef = db.getReference(fbref.quizConfig)

        checkQuizValidity()
        loadCurrentQuestion()
    }


    fun loadCurrentQuestion(){
            quesRef.child(fbref.queStatus().currentQue).
            child(fbref.queStatus().index)
                .addValueEventListener(currentQuizQueListner)

    }

    fun checkQuizValidity() {
        configRef.child(fbref.opConfig_isStarted)
            .addValueEventListener(validityListner)


    }

    fun observeQuizValidity() : LiveData<Int> {
        return quizStatusResponseCode
    }

    fun observeCurrentQuestion() : LiveData<Int
            > {
        return currentQuestionIndex
    }
}