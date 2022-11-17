package com.ncs.quizr.dataClasses

class Questions {


    fun getQuestions(): ArrayList<Question>{

        //Programming Club
        val questionList = ArrayList<Question>()//Creating an empty arraylist
        val queArrProgClub= arrayListOf("How to center a div","Which one of these is the most used programming lang"
        ,"Android is based on which kernel ?","Java compiler based or interpreted language?","Ek aur question!")
        val isMCQProgClub = arrayListOf(false,true,false,false,false)
        val mcQsolutionsProg: HashMap<Int,Array<String>> = HashMap()
        mcQsolutionsProg.put(2, arrayOf("Java","Kotlin","JavaScript","Python"))

        //Web Club
        val queArrWebClub= arrayListOf("How to center a div","Which one of these is the most used programming lang"
            ,"Android is based on which kernel ?","Java compiler based or interpreted language?","Ek aur question!")
        val isMCQWebClub = arrayListOf<Boolean>(false,true,false,false,false)
        val mcQsolutionsWeb: HashMap<Int,Array<String>> = HashMap()
        mcQsolutionsProg.put(2, arrayOf("Java","Kotlin","JavaScript","Python"))


        val totalQues = 10

        for(i in 0 until totalQues){
            val index = i+1
            if (index in 1..5) {
                if (isMCQProgClub[i]==true) {

                    questionList.add(Question(index, "Programming", queArrProgClub[i], isMCQProgClub[i],mcQsolutionsProg.get(index)))
                }else {
                    questionList.add(Question(index, "Programming", queArrProgClub[i], isMCQProgClub[i],null))
                }

            }else if (index in 6..10){
                var j = i-5
                if (isMCQWebClub[j]==true) {

                    questionList.add(Question(index, "Web", queArrWebClub[j], isMCQWebClub[j],mcQsolutionsWeb.get(index-6)))
                }else {
                    questionList.add(Question(index, "Web", queArrWebClub[j], isMCQWebClub[j],null))
                }
            }
        }

        return questionList
    }

}