package com.ncs.quizr.quiz

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ncs.quizr.R
import com.ncs.quizr.dataClasses.Question
import com.ncs.quizr.dataClasses.Questions
import com.ncs.quizr.dataClasses.QuizModelConstants
import com.ncs.quizr.databinding.ActivityQuizBinding
import org.w3c.dom.Text

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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        questionsList = queRepo.getQuestions()
        model = ViewModelProvider(this)[QuizActivityViewModel::class.java]

        initViews()
        model.initModel()
        doQuizValidation()
        loadCurrentQuestion()


    }

    private fun loadCurrentQuestion() {
        model.observeCurrentQuestion().observe(this) {
            index->

            if (index!=-404){
                val question = questionsList.get(index-1)
                setQuizView(question)

            }else{
                Toast.makeText(baseContext, "Technical error, restart!", Toast.LENGTH_SHORT).show()
                finish()
            }


        }
    }

    fun setQuizView(question:Question){

        var queIndex = question.Qindex
        var quesContent = question.question
        var club = question.club
        val isMcq = question.isMCQ
        var mcqList: Array<String>? = null

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