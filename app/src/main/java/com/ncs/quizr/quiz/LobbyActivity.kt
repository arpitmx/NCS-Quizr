package com.ncs.quizr.quiz

import android.R.id
import android.app.Dialog
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.datatransport.runtime.dagger.internal.SingleCheck
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ncs.quizr.R
import com.ncs.quizr.dataClasses.QuizModelConstants
import com.ncs.quizr.dataClasses.leaderboardUser
import com.ncs.quizr.dataClasses.realTimeDatabaseRefPaths
import com.ncs.quizr.dataClasses.sharedPrefsKeys
import com.ncs.quizr.databinding.ActivityLobbyBinding


class LobbyActivity : AppCompatActivity() {

    val fbref: realTimeDatabaseRefPaths = realTimeDatabaseRefPaths()
    val mc: QuizModelConstants = QuizModelConstants()
    lateinit var sharedPref : SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    private lateinit var db: FirebaseDatabase
    private lateinit var ref : DatabaseReference
    val prefdb : sharedPrefsKeys = sharedPrefsKeys()
    val TAG = "LobbyActivity"
    lateinit var extras : Bundle
    lateinit var binding:ActivityLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = getSharedPreferences(prefdb.sharedPrefID, MODE_PRIVATE)
        editor = sharedPref.edit()



        db = Firebase.database
        ref = db.getReference(fbref.quizConfig).child(fbref.queStatus().currentQue)
        extras = intent.extras!!

        initViews()


    }


    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.back_dialog)
        val body = dialog.findViewById(R.id.tvBody) as TextView
        body.text="Do not close this window, your response will be deleted, remain on this."
        val yesBtn = dialog.findViewById(R.id.btn_yes) as AppCompatButton
        val noBtn = dialog.findViewById(R.id.btn_no) as AppCompatButton

        yesBtn.text = "Okay"
        noBtn.text = "Leave quiz"
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        noBtn.setOnClickListener{
            finish()
        }
        dialog.show()


    }

    override fun onBackPressed() {
        showDialog()
    }


    private val winnerListener = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("email")
                val email = snapshot.child("username")

        }


        override fun onCancelled(error: DatabaseError) {
            Log.d(TAG, "Value is: Error")
        }


    }

    fun checkIfIamAWinner(){
        ref.child(fbref.queStatus().winner).addValueEventListener(statusListner)

    }


    private fun initViews() {

        checkIfIamAWinner()
        if (extras.getBoolean("timeout")){
            val timeTaken = extras.getLong("time")
            binding.isCorrect.text = "Time out!, better luck next time"
            binding.timeTaken.text = "Time taken : ${timeTaken/1000} sec"
        }
        else if(extras.getBoolean("reOpened") == true){
            binding.isCorrect.text = "Reopening quizes is prohibited, attend next quiz"
        }else {

            val isCorrect = extras.getBoolean("isCorrect")
            val index = extras.getInt("index")
            Firebase.auth.currentUser?.email
            val timeTaken = extras.getLong("time")
            val isSuper = extras.getBoolean("isSuper")

            if (isSuper){
                binding.isCorrect.text = "That was a super question and was meant to be spoken in the OP"
            }else {
                if (isCorrect) {
                    binding.isCorrect.text = "Correct Solution"
                    binding.isCorrect.setTextColor(resources.getColor(R.color.green))
                } else {
                    binding.isCorrect.text = "Wrong Solution"
                    binding.isCorrect.setTextColor(resources.getColor(R.color.red))
                }
            }
            binding.QueNum.text = "Question ${index}"
            binding.timeTaken.text = "Time taken : ${timeTaken/1000} sec"

            if (isCorrect){
                val username = sharedPref.getString(prefdb.username,"usrname-ERR-nousrname").toString()
                addToLeaderBoard(username,timeTaken)
            }else {
                ref.child(fbref.queStatus().totalSub).addValueEventListener(statusListner)
            }
        }

    }

    private val statusListner = object : ValueEventListener {


        override fun onDataChange(dataSnapshot: DataSnapshot) {
           if (!addedToSub) {
               addedToSub= true
               val subnum = Integer.parseInt(dataSnapshot.value.toString())
               ref.child(fbref.queStatus().totalSub).setValue(subnum + 1)
           }
        }
        override fun onCancelled(error: DatabaseError) {
            Log.d(TAG, "Value is: Error")
        }


    }

    var addedToSub: Boolean = false

    fun addToLeaderBoard( username :String , time : Long ){

        var userDetail : HashMap<String,leaderboardUser>  = hashMapOf(
             username to leaderboardUser(Firebase.auth.currentUser!!.email.toString(),time)
        )

        ref.child(fbref.queStatus().correctSub).child(fbref.queStatus().submitters)
            .setValue(userDetail)
            .addOnCompleteListener{
                Toast.makeText(this, "Submitted to server!", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener {
                Toast.makeText(this, "Submission failure due to ${it.message}", Toast.LENGTH_SHORT).show()
            }

        ref.child(fbref.queStatus().totalSub).addValueEventListener(statusListner)

    }


}