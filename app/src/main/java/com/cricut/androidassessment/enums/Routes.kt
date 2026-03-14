package com.cricut.androidassessment.enums

import com.cricut.androidassessment.enums.NavigationConstants.NAV_ARG_QUIZ_ID

object Routes {
    const val ASSESSMENT = "assessment"
    const val QUIZ = "quiz/{${NAV_ARG_QUIZ_ID}}"

    fun generateQuizRoute(quizId: Int): String {
        return "quiz/$quizId"
    }
}

object NavigationConstants {
    const val NAV_ARG_QUIZ_ID = "quiz_id"

}

