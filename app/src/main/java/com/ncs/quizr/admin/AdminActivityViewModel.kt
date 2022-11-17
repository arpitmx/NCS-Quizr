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


    fun setQuestion(index: String){
        quesRef.child(fbref.queStatus().currentQue).child(fbref.queStatus().index)
            .setValue(index)
            .addOnCompleteListener{
                isQueSetLiveData.value = 200
            }.addOnFailureListener {
                isQueSetLiveData.value = 404
            }
    }

    fun getStatusResponse(): LiveData<String> {
        return opStatus
    }
    fun getIsQueSetAtIndex(): LiveData<Int> {
        return isQueSetLiveData
    }

}