package com.cricut.androidassessment.data

import com.cricut.androidassessment.model.MultipleChoiceQuestion
import com.cricut.androidassessment.model.Quiz
import com.cricut.androidassessment.model.QuizQuestion
import com.cricut.androidassessment.model.TrueFalseQuestion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor() {
    private val questionOne = TrueFalseQuestion(
        id = 1,
        questionText = "True or False: A Composable function can return a value?",
        correctAnswer = false
    )
    private val questionTwo = MultipleChoiceQuestion(
        id = 2,
        questionText = "What is the primary language recommended by Google for Android development?",
        options = listOf("Java", "Kotlin", "Swift", "Dart"),
        correctAnswerIndex = 1
    )
    private val questions: List<QuizQuestion> = listOf(
        questionOne, questionTwo
    )

    private val quiz = Quiz(
        id = 1,
        title = "Sample Quiz",
        questions = questions
    )

    private val quizzes: List<Quiz> = listOf(quiz)

    fun getQuizzes(): Flow<List<Quiz>> {
        return flowOf(quizzes)
    }

    fun getQuizById(id: Int): Quiz? {
        return quizzes.find { it.id == id }
    }
}
