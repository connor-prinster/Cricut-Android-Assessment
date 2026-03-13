package com.cricut.androidassessment.enums

import android.net.Uri
import com.cricut.androidassessment.enums.NavigationConstants.NAV_ARG_QUIZ_ID

object Routes {
    const val ASSESSMENT = "assessment"
    const val QUIZ = "quiz/{${NAV_ARG_QUIZ_ID}}"

    fun generateQuizRoute(questionId: Int): String {
        return QUIZ.replace(NAV_ARG_QUIZ_ID, Uri.encode(questionId.toString()))
    }
}

object NavigationConstants {
    const val NAV_ARG_QUIZ_ID = "quiz_id"

}

