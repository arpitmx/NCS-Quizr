package com.ncs.quizr.dataClasses

 class realTimeDatabaseRefPaths() {

     val opConfig: String = "OP_CONFIG_DEBUG"
     val quizConfig: String = "QUIZ_CONFIG_DEBUG"
    val opConfig_isStarted: String = "STATUS"

     inner class opStatus {
         val opStarted: String = "_STARTED_"
         val opEnded: String = "_ENDED_"
         val opNotStarted: String = "_NOTSTARTED_"
     }

     inner class update {
         val opStarted: String = "_STARTED_"
         val appConfig: String = "APP_CONFIG_DEBUG"
         val updateLink: String = "UPDATE_LINK"
         val noUpdateLink: String = "NO-UPDATE"
         val Update: String = "UPDATE"


     }

     inner class queStatus {
         val currentQue: String = "CURRENT_QUES"
         val index : String = "INDEX"
         val correctSub : String = "CORRECT_SUBS"
         val totalSub : String = "TOTAL_SUB"
         val winner : String = "WINNER"
         val submitters : String = "SUBMITTERS"
         val questionStatus : String = "QUE_STATUS"
         val counting : String = "_COUNTING_"
         val over : String = "_OVER_"

     }

}