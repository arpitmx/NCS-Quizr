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
import com.ncs.quizr.dataClasses.Question
import com.ncs.quizr.dataClasses.Questions
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
    var currentQueIndex :String = "-1"
    var questionList : ArrayList<Question> = Questions().getQuestions()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        model = ViewModelProvider(this)[AdminActivityViewModel::class.java]
        sharedPref = getSharedPreferences("admin", MODE_PRIVATE)
        editor = sharedPref.edit()

        model.init()
        initViews()
        initQuiz()
    }

    private fun initQuiz() {
        var lastQueIndex = sharedPref.getString("lastQueIndex","null")

        if (lastQueIndex!="null"){
            setQuestionAtIndex(lastQueIndex!!)

            editor.putString("lastQueIndex",lastQueIndex)
            editor.apply()
            editor.commit()
        }else{

        currentQueIndex = "1"
        editor.putString("lastQueIndex","1")
        editor.apply()
        editor.commit()
            setQuestionAtIndex(currentQueIndex)

        }


    }

    fun vibrate(){
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(100)
    }

    fun setQuestionAtIndex(index:String){
        currentQueIndex = index
        model.setQuestion(index)
        setQuizTextViewDetails()
        model.getIsQueSetAtIndex().observe(this){
            if (it == 200){
                Toast.makeText(this, "Question ${index} set!", Toast.LENGTH_SHORT).show()
                binding.questionIndexEditBox.text.clear()
            }else {
                Toast.makeText(this, "Failure setting que ${index} set!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun setQuizTextViewDetails(){
        val question = questionList.get(currentQueIndex.toInt())
        binding.quizDetailTextView.text =
            "Quiz details :\n\n Question No. : ${currentQueIndex} \n Club : ${question.club} \n Question : ${question.question}"



    }

    private fun initViews() {

        initQuiz()

        model.getTotalSubs()

        model.getTotalSubLiveData().observe(this){
            binding.submitted.text= "Total Submissions : ${it}"
        }


        dialog = BottomSheetDialog(this)

        model.setHighest()
        model.getStatusResponse().observe(this){
            binding.qstatus.text= "Quiz Status :  ${it}"
        }

        binding.setQue.setOnClickListener{
            vibrate()
          if (binding.questionIndexEditBox.text.isNotEmpty()){
                val index = binding.questionIndexEditBox.text.toString()
                setQuestionAtIndex(index)
            }
        }

        model.getHighestPlayer().observe(this){
            binding.highestScorer.text = "Highest scorer : ${it.toString()}"
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