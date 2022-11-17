package com.ncs.quizr.admin

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ncs.quizr.R
import com.ncs.quizr.dataClasses.QuizModelConstants
import com.ncs.quizr.dataClasses.realTimeDatabaseRefPaths
import com.ncs.quizr.databinding.ActivityAdminBinding
import com.ncs.quizr.databinding.ActivityAuthBinding

class AdminActivity : AppCompatActivity() {

    lateinit var binding :ActivityAdminBinding
    lateinit var dialog : BottomSheetDialog
    lateinit var model : AdminActivityViewModel
    val fbref: realTimeDatabaseRefPaths = realTimeDatabaseRefPaths()
    val mc: QuizModelConstants = QuizModelConstants()
    lateinit var sharedPref : SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = ViewModelProvider(this)[AdminActivityViewModel::class.java]
        sharedPref = getSharedPreferences("admin", MODE_PRIVATE)
        editor = sharedPref.edit()

        initViews()
        initQuiz()
    }

    private fun initQuiz() {

    }

    fun vibrate(){
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)
    }
    private fun initViews() {
        dialog = BottomSheetDialog(this)
        model.init()

        model.getStatusResponse().observe(this){
            binding.qstatus.text= "Quiz Status :  ${it}"
        }

        binding.setQue.setOnClickListener{
            vibrate()
          if (binding.questionIndexEditBox.text.isNotEmpty()){
                val index = binding.questionIndexEditBox.text.toString()
                model.setQuestion(index)
                model.getIsQueSetAtIndex().observe(this){
                    if (it == 200){
                        Toast.makeText(this, "Question ${index} set!", Toast.LENGTH_SHORT).show()
                        binding.questionIndexEditBox.text.clear()
                    }else {
                        Toast.makeText(this, "Failure setting que ${index} set!", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }

        binding.quizStatus.setOnClickListener{
            val view = layoutInflater.inflate(R.layout.quizstatus_btm, null)
            dialog.setCancelable(true)
            dialog.dismissWithAnimation = true
            dialog.setContentView(view)
            dialog.show()

            val startQuizbtn = view.findViewById<AppCompatButton>(R.id.startMainquizbtn)
            val pauseQuizBtn = view.findViewById<AppCompatButton>(R.id.stopQuizbtn)
            val endQuizBtn = view.findViewById<AppCompatButton>(R.id.endQuizbtn)

            startQuizbtn.setOnClickListener{
                model.setQuizStatus(fbref.opStatus().opStarted)
                Toast.makeText(this, "Quiz started!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            pauseQuizBtn.setOnClickListener{
                model.setQuizStatus(fbref.opStatus().opNotStarted)
                Toast.makeText(this, "Quiz Paused!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

            }

            endQuizBtn.setOnClickListener{
                model.setQuizStatus(fbref.opStatus().opEnded)
                Toast.makeText(this, "Quiz Ended!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

            }


        }
    }
}