package com.ncs.quizr.quiz

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.ncs.quizr.R
import com.ncs.quizr.dataClasses.Question
import com.ncs.quizr.dataClasses.Questions
import com.ncs.quizr.dataClasses.QuizModelConstants
import com.ncs.quizr.databinding.ActivityQuizBinding
import java.util.*
import kotlin.concurrent.schedule

class QuizActivity : AppCompatActivity() {

    lateinit var binding : ActivityQuizBinding
    lateinit var clubTitle : TextView
    lateinit var questionNumber: TextView
    lateinit var questionBox: TextView
    lateinit var answerBox: EditText
    lateinit var mcqGroup: RadioGroup
    lateinit var submitBtn : AppCompatButton
    private lateinit var model: QuizActivityViewModel
    val constants : QuizModelConstants = QuizModelConstants()
    lateinit var questionsList: ArrayList<Question>
    private val queRepo: Questions = Questions()
    lateinit var countDownTimer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // binding.questionBox.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))

        questionsList = queRepo.getQuestions()
        model = ViewModelProvider(this)[QuizActivityViewModel::class.java]

        loader(1)
        initViews()
        model.initModel()
        doQuizValidation()
        loadCurrentQuestion()


    }

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val notConnected = intent.getBooleanExtra(ConnectivityManager
                .EXTRA_NO_CONNECTIVITY, false)
            if (notConnected) {
                loader(1)
                Toast.makeText(baseContext, "Low connection, please check your internet!", Toast.LENGTH_SHORT).show()
            } else {
                loader(0)
                Toast.makeText(baseContext, "Stable connection!", Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun loader(show:Int){
        when(show){
            1->{
                binding.loaderView.visibility = View.VISIBLE
                binding.loaderView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pop_in))
                binding.quizView.visibility= View.GONE
                binding.quizView.clearAnimation()
            }
            0->{

                binding.linearProg.visibility= View.VISIBLE
//                Timer("SettingUp", false).schedule(3000) {
//
//                    runOnUiThread {
//                        binding.linearProg.visibility= View.GONE
//                    }
//
//                }

                binding.loaderView.visibility= View.GONE
                binding.loaderView.clearAnimation()
                binding.quizView.visibility= View.VISIBLE
                binding.quizView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
            }
            2->{

                    binding.loaderView.visibility = View.VISIBLE
                    binding.loaderView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pop_in))
                    binding.progText.text = "Timeout⌛\nWaiting for next question..."
                    binding.quizView.visibility= View.GONE
                    binding.quizView.clearAnimation()

            }

            3->{

                binding.loaderView.visibility = View.VISIBLE
                binding.loaderView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pop_in))
                binding.progText.text = "In time ⚡ \nWaiting for next question..."
                binding.quizView.visibility= View.GONE
                binding.quizView.clearAnimation()
                countDownTimer.cancel()

            }

        }
    }

    fun vibrate(){
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500)
    }

    private fun loadCurrentQuestion() {
        model.observeCurrentQuestion().observe(this) {
            index->

            if (index>0&&index<=20){
                val question = questionsList.get(index-1)
                setQuizView(question)

            }else{
                Toast.makeText(baseContext, "Technical error, restart!", Toast.LENGTH_SHORT).show()
                finish()
            }


        }
    }

    fun setQuizView(question:Question){

        //countDownTimer.cancel()

        var queIndex = question.Qindex
        var quesContent = question.question
        var club = question.club
        val isMcq = question.isMCQ
        var mcqList: Array<String>?

        if (isMcq){
            mcqList = question.mcqList
            mcqGroup.visibility = View.VISIBLE
            answerBox.visibility = View.GONE

            binding.option1.text = mcqList!![0]
            binding.option2.text = mcqList[1]
            binding.option3.text = mcqList[2]
            binding.option4.text = mcqList[3]



        }else{
            mcqGroup.visibility = View.GONE
            answerBox.visibility= View.VISIBLE
        }


        clubTitle.text = "${club} Club"
        questionNumber.text = "Question ${queIndex}"
        questionBox.text = quesContent


        loader(0)
        startCountDown(30)

    }


    fun startCountDown(time : Long){
        var i : Int = 0
       binding.linearProg.progress = 0
       countDownTimer = object : CountDownTimer(time*1000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                Log.v("Log_tag", "Tick of Progress$i$millisUntilFinished")
                i++
                binding.linearProg.setProgress((i * 100 / (time*1000 / 1000)).toInt())
                binding.timeleftText.setText("${millisUntilFinished / 1000} sec left:")

            }

            override fun onFinish() {
                //Do what you want
                i++
                binding.linearProg.setProgress(100)
                loader(2)
                vibrate()
            }
        }
        countDownTimer.start()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    fun initViews(){
        clubTitle = binding.clubTitle
        questionBox = binding.questionBox
        questionNumber = binding.questionNum
        answerBox = binding.answerBox
        mcqGroup = binding.mcqGroup
        submitBtn = binding.submitBtn

        submitBtn.setOnClickListener{
            loader(3)
        }


    }

    fun doQuizValidation(){
        model.observeQuizValidity().observe(this) { responseCode ->
            when(responseCode){
                constants.Validity().running-> {
                    Toast.makeText(baseContext, "GO GO GO!", Toast.LENGTH_SHORT).show()
                }
                constants.Validity().notStarted->{
                    Toast.makeText(baseContext, "Unauthorised Quiz,halt.", Toast.LENGTH_SHORT).show()
                    finish()
                }
                constants.Validity().finished->{
                    Toast.makeText(baseContext, "Quiz ended by the admin!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                constants.Validity().error->{
                    Toast.makeText(baseContext, "Technical error, restart!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else ->{
                    Toast.makeText(baseContext, "Invalid response code, restart!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}