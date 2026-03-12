package com.cricut.androidassessment.data

import com.cricut.androidassessment.model.MultipleChoiceQuestion
import com.cricut.androidassessment.model.QuizQuestion
import com.cricut.androidassessment.model.TrueFalseQuestion
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor() {
    fun getQuestions(): List<QuizQuestion> {
        return listOf(
            TrueFalseQuestion(
                id = 1,
                questionText = "True or False: A Composable function can return a value?",
                correctAnswer = false
            ),
            MultipleChoiceQuestion(
                id = 2,
                questionText = "What is the primary language recommended by Google for Android development?",
                options = listOf("Java", "Kotlin", "Swift", "Dart"),
                correctAnswerIndex = 1
            )
        )
    }
}
