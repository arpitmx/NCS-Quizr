package com.ncs.quizr.dataClasses

class Questions {


    fun getQuestions(): ArrayList<Question>{

        //Programming Club
        val questionList = ArrayList<Question>()//Creating an empty arraylist
        val queArrProgClub= arrayListOf("What is the first computer virus?","Name the two cofounder of Apple Inc."
        ,"Guess the programming language's logos","Super Question \n Which programming language is used for writing the highest number of powerful and malicious virus")
        val solArrProgClub= arrayListOf("creeper","Steve Jobs and Steve Wozniak","ruby rust kotlin java dart","c")
        val isMCQProgClub = arrayListOf(false,true,false,true)
        val mcQsolutionsProg: HashMap<Int,Array<String>> = HashMap()
        mcQsolutionsProg.put(2, arrayOf("Steve Jobs and Steve Wozniak","Steve Jobs and Steve Reynolds","Steve Jobs and Steve Martin","Steve Jobs and Steve Walker"))
        mcQsolutionsProg.put(4, arrayOf("C","Python","Fortran","C++"))

        //Web Club
        val queArrWebClub= arrayListOf("What is the full form of URL","What is the full form of HTML and CSS"
            ,"How many heading tags available in HTML ?","Super question \n Identify the icons of Frameworks and Libraries used for the frontend development")
        val isMCQWebClub = arrayListOf<Boolean>(false,true,false,false)
        val mcQsolutionsWeb: HashMap<Int,Array<String>> = HashMap()
        mcQsolutionsWeb.put(2, arrayOf("Hyper Text Markup Language, Cascading Style Sheet","Hyper Text Mark Lexter, Cascading Style Setter","Hyper Text Markup Language, Corner Style Sheet","Hyper Text Markup Language, Cascader Style Sheet"))
        val solArrWebClub= arrayListOf("uniform resource locator","Hyper Text Markup Language, Cascading Style Sheet","Six","69")

        //Tech Club
        val queArrTechClub= arrayListOf("In 2020, a cyberattack happened affecting the major 500 fortune companies so much that they needed to shut down their services for server hours",
            "Recently, a cyberattack shoot 4.5 Million passenger's data from a popular Indian airline, name the airline"
            ,"Audience question \n What are the alphabet of Android versions","Super question \n " +
                    "Why facebook was fined for Cambridge analytica scandal?")
        val isMCQTechClub = arrayListOf<Boolean>(true,false,false,false)
        val mcQsolutionsTech: HashMap<Int,Array<String>> = HashMap()
        mcQsolutionsWeb.put(1, arrayOf("Solarwinds attack","Petya attack","Wannacry ransom attack","Cryptolocker attack"))
        val solArrTechClub= arrayListOf("Solarwinds attack","Air India","Linux","69")



        val totalQues = 12

        for(i in 0 until totalQues){
            val index = i+1
            if (index in 1..4) {
                if (isMCQProgClub[i]) {
                    questionList.add(Question(index, "Programming", queArrProgClub[i], isMCQProgClub[i],mcQsolutionsProg.get(index),solArrProgClub[i].lowercase()))
                }else {
                    questionList.add(Question(index, "Programming", queArrProgClub[i], isMCQProgClub[i],null,solArrProgClub[i].lowercase()))
                }

            }else if (index in 5..8){
                val j = i-4
                if (isMCQWebClub[j]==true) {

                    questionList.add(Question(index, "Web", queArrWebClub[j], true,mcQsolutionsWeb.get(index-5),solArrWebClub[j].lowercase()))
                }else {
                    questionList.add(Question(index, "Web", queArrWebClub[j], isMCQWebClub[j],null,solArrWebClub[j].lowercase()))
                }
            }else if (index in 9..12){
                val k = i-8
                if (isMCQWebClub[k]==true) {

                    questionList.add(Question(index, "Technical", queArrTechClub[k], true,mcQsolutionsWeb.get(index-9),solArrWebClub[k].lowercase()))
                }else {
                    questionList.add(Question(index, "Technical", queArrTechClub[k], isMCQTechClub[k],null,solArrWebClub[k].lowercase()))
                }
            }
        }

        return questionList
    }

}