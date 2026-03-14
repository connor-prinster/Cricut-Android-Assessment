package com.cricut.androidassessment.model

data class Quiz(
    val id: Int,
    val title: String,
    val questions: List<QuizQuestion>
) {
    companion object {
        fun generateTest(): Quiz {
            return Quiz(
                id = 1,
                title = "Sample Quiz",
                questions = listOf(
                    TrueFalseQuestion(
                        id = 1,
                        questionText = "This is a true question?",
                        correctAnswer = true
                    )
                )
            )
        }
    }
}
