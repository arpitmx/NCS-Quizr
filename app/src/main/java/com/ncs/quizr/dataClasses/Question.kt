package com.ncs.quizr.dataClasses

data class Question(val Qindex: Int,
                    val club: String, val question: String,
                    val isMCQ: Boolean, val mcqList: Array<String>?, val ans : String)