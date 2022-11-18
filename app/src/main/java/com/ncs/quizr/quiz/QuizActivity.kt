package com.ncs.quizr.quiz

import android.app.Dialog
import android.content.*
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Vibrator
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ncs.quizr.R
import com.ncs.quizr.dataClasses.Question
import com.ncs.quizr.dataClasses.Questions
import com.ncs.quizr.dataClasses.QuizModelConstants
import com.ncs.quizr.dataClasses.sharedPrefsKeys
import com.ncs.quizr.databinding.ActivityQuizBinding
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
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
    private var seconds = 0

    // Is the stopwatch running?
    private var running = false

    private var wasRunning = false

    private var timeStarted : Long = 0
    private var timeElapsed : Long = 0

    lateinit var sharedPref : SharedPreferences
    lateinit var editor: SharedPreferences.Editor
    val prefDB = sharedPrefsKeys()

    private fun broadCastTime(timeElapsed: Long) {
        Toast.makeText(this@QuizActivity, "Time taken : "+timeElapsed.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun startTimer() {
        timeStarted = System.currentTimeMillis()

    }
    private fun stopTimer(){
        timeElapsed = System.currentTimeMillis() - timeStarted
        broadCastTime(timeElapsed)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

       // binding.questionBox.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
        sharedPref = getSharedPreferences(prefDB.sharedPrefID, MODE_PRIVATE)
        editor = sharedPref.edit()

        if (savedInstanceState != null) {

            // Get the previous state of the stopwatch
            // if the activity has been
            // destroyed and recreated.
            seconds = savedInstanceState
                .getInt("seconds")
            running = savedInstanceState
                .getBoolean("running")
            wasRunning = savedInstanceState
                .getBoolean("wasRunning")
        }



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

            //Toast.makeText(baseContext, "Low connection, please check your internet!", Toast.LENGTH_SHORT).show()
            } else {
                loader(0)
                //Toast.makeText(baseContext, "Stable connection!", Toast.LENGTH_SHORT).show()

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
                Timer("SettingUp", false).schedule(3000) {

                    runOnUiThread {
                        binding.linearProg.visibility= View.GONE
                    }

                }

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
    fun validDateReopen(question: Question){

        val lastQue = sharedPref.getString("lastIndex","-1")

        if (lastQue!="-1"){
            if (lastQue==question.Qindex.toString()){
                Toast.makeText(this, "Your reopened this question", Toast.LENGTH_SHORT).show()
                val lobbyIntent = Intent(this, LobbyActivity::class.java)
                lobbyIntent.putExtra("reOpened", true)
                startActivity(lobbyIntent)
                finish()
            }


        }
            editor.putString("lastIndex",question.Qindex.toString())
            editor.apply()
            editor.commit()


    }

    fun setQuizView(question:Question){


        //countDownTimer.cancel()

        validDateReopen(question)

        var queIndex = question.Qindex
        var quesContent = question.question
        var club = question.club
        val isMcq = question.isMCQ
        var mcqList: Array<String>?
        var ans = question.ans
        val isSuper: Boolean = question.ans == "69"
        questionNumber.text = "Question ${queIndex}"


        if (isMcq){
            mcqList = question.mcqList
            mcqGroup.visibility = View.VISIBLE
            answerBox.visibility = View.GONE

            binding.option1.text = mcqList!![0]
            binding.option2.text = mcqList[1]
            binding.option3.text = mcqList[2]
            binding.option4.text = mcqList[3]


        }else {
            if (isSuper){
                answerBox.visibility= View.GONE
                questionNumber.text = "Super Question ${queIndex}"
            }else {
                answerBox.visibility= View.VISIBLE
            }
            mcqGroup.visibility = View.GONE
        }


        clubTitle.text = "${club} Club"
        questionBox.text = quesContent

        handleSubmit(question, isSuper)
        loader(0)
        startTimer()

    }
    fun onClickStop(view: View?) {
        running = false
    }


    override fun onResume() {
        super.onResume()
    }

    private fun handleSubmit(question:Question, isSuper:Boolean) {

        binding.submitBtn.setOnClickListener {
            val lobbyIntent = Intent(this, LobbyActivity::class.java)

            if (isSuper) {
                Toast.makeText(this, "This is an super question", Toast.LENGTH_SHORT).show()
                lobbyIntent.putExtra("isSuper", true)
                lobbyIntent.putExtra("index", question.Qindex)
                startActivity(lobbyIntent)
                finish()

            } else {

                if (question.isMCQ) {
                    val option = question.ans
                    val selectedOption: Int = mcqGroup.checkedRadioButtonId
                    val rbtn = findViewById<RadioButton>(selectedOption)
                    if (rbtn != null) {

                        val rb = rbtn.text.toString().lowercase()
                        if (rb == option) {
                            stopTimer()
                            lobbyIntent.putExtra("isCorrect", true)

                        } else {
                            stopTimer()
                            lobbyIntent.putExtra("isCorrect", false)
                        }

                        lobbyIntent.putExtra("isSuper", false)

                        lobbyIntent.putExtra("index", question.Qindex)
                        lobbyIntent.putExtra("time", timeElapsed)
                        lobbyIntent.putExtra("reOpened", false)


                        startActivity(lobbyIntent)
                        finish()


                    } else {
                        Toast.makeText(this, "Choose a response first!", Toast.LENGTH_SHORT).show()
                    }


                } else {
                    val ans = question.ans
                    if (!binding.answerBox.text.isBlank()) {
                        val response = binding.answerBox.text.toString()
                        if (response == ans) {
                            stopTimer()
                            lobbyIntent.putExtra("isCorrect", true)


                        } else {
                            stopTimer()
                            lobbyIntent.putExtra("isCorrect", false)
                        }

                        lobbyIntent.putExtra("isSuper", false)
                        lobbyIntent.putExtra("index", question.Qindex)
                        lobbyIntent.putExtra("time", timeElapsed)
                        lobbyIntent.putExtra("reOpened", false)


                        startActivity(lobbyIntent)
                        finish()
                    } else {
                        binding.answerBox.error = "Fill in answer first!"
                    }
                }




            }


        }
    }

//
//    fun startCountDown(){
//       val time:Long = 30
//        var i = 0
//       binding.linearProg.progress = 0
//       countDownTimer = object : CountDownTimer(time*1000, 1000) {
//
//
//
//            override fun onTick(millisUntilFinished: Long) {
//                Log.v("Log_tag", "Tick of Progress$i$millisUntilFinished")
//                i++
//                binding.linearProg.setProgress((i * 100 / (time*1000 / 1000)).toInt())
//                binding.timeleftText.setText("${millisUntilFinished / 1000} sec left:")
//
//            }
//
//            override fun onFinish() {
//                //Do what you want
//                i++
//                binding.linearProg.setProgress(100)
//                loader(2)
//                vibrate()
//            }
//
//
//        }
//
//    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)

        dialog.setContentView(R.layout.back_dialog)

        val yesBtn = dialog.findViewById(R.id.btn_yes) as AppCompatButton
        val noBtn = dialog.findViewById(R.id.btn_no) as AppCompatButton

        yesBtn.setOnClickListener {
            dialog.dismiss()
            Toast.makeText(this, "Quiz closed!", Toast.LENGTH_SHORT).show()
            finish()
        }
        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }

    override fun onBackPressed() {
        showDialog()

    }

    fun initViews(){
        clubTitle = binding.clubTitle
        questionBox = binding.questionBox
        questionNumber = binding.questionNum
        answerBox = binding.answerBox
        mcqGroup = binding.mcqGroup
        submitBtn = binding.submitBtn




    }

    fun doQuizValidation(){
        model.observeQuizValidity().observe(this) { responseCode ->
            when(responseCode){
                constants.Validity().running-> {
                   // Toast.makeText(baseContext, "GO GO GO!", Toast.LENGTH_SHORT).show()
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