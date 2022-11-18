package com.ncs.quizr.dataClasses

 class realTimeDatabaseRefPaths() {

     val opConfig: String = "OP_CONFIG"
     val quizConfig: String = "QUIZ_CONFIG"
    val opConfig_isStarted: String = "STATUS"

     inner class opStatus {
         val opStarted: String = "_STARTED_"
         val opEnded: String = "_ENDED_"
         val opNotStarted: String = "_NOTSTARTED_"
     }

     inner class queStatus {
         val currentQue: String = "CURRENT_QUES"
         val index : String = "INDEX"
         val correctSub : String = "CORRECT_SUBS"
         val totalSub : String = "TOTAL_SUB"
         val winner : String = "WINNER"
         val submitters : String = "SUBMITTERS"


     }

}