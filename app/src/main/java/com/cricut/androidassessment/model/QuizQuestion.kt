package com.cricut.androidassessment.model

sealed interface QuizQuestion {
    val id: Int
    val questionText: String
}

data class TrueFalseQuestion(
    override val id: Int,
    override val questionText: String,
    val correctAnswer: Boolean
) : QuizQuestion

data class MultipleChoiceQuestion(
    override val id: Int,
    override val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int
) : QuizQuestion

data class MultipleSelectionQuestion(
    override val id: Int,
    override val questionText: String,
    val options: List<String>,
    val correctAnswerIndices: Set<Int>
) : QuizQuestion

data class OpenEndedQuestion(
    override val id: Int,
    override val questionText: String,
    val correctAnswer: String
) : QuizQuestion
