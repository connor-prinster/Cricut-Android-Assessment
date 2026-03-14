package com.cricut.androidassessment.ui.screens.question

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.ext.LOGGING_TAG
import com.cricut.androidassessment.model.Quiz
import com.cricut.androidassessment.model.QuizQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val quizFlow: StateFlow<Quiz?>,
    val currentQuestionIndexFlow: StateFlow<Int>,
    val answersFlow: StateFlow<Map<Int, Any?>>,
    val isFinishedFlow: StateFlow<Boolean>,
    val currentQuestionFlow: StateFlow<QuizQuestion?>,
    val isFirstQuestionFlow: StateFlow<Boolean>,
    val isLastQuestionFlow: StateFlow<Boolean>,
    val isNextEnabledFlow: StateFlow<Boolean>,

    val onAnswerSelected: (Int, Any?) -> Unit,
    val navigateNext: () -> Unit,
    val restartQuiz: () -> Unit,
    val navigateBack: () -> Unit
) {
    companion object {
        val EMPTY = QuizUiState(
            quizFlow = MutableStateFlow(null),
            currentQuestionIndexFlow = MutableStateFlow(0),
            answersFlow = MutableStateFlow(mapOf()),
            isFinishedFlow = MutableStateFlow(false),
            currentQuestionFlow = MutableStateFlow(null),
            isFirstQuestionFlow = MutableStateFlow(true),
            isLastQuestionFlow = MutableStateFlow(false),
            isNextEnabledFlow = MutableStateFlow(false),

            onAnswerSelected = { _, _ -> },
            navigateNext = {},
            restartQuiz = {},
            navigateBack = {}
        )
    }
}


@HiltViewModel
class QuizScreenViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val quizIdFlow: MutableStateFlow<Int?> = MutableStateFlow(null)
    private val quizFlow: MutableStateFlow<Quiz?> = MutableStateFlow(null)
    private val currentQuestionIndexFlow: MutableStateFlow<Int> = MutableStateFlow(0)
    private val answersFlow: MutableStateFlow<Map<Int, Any?>> = MutableStateFlow(emptyMap())
    private val isFinishedFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val currentQuestionFlow: MutableStateFlow<QuizQuestion?> = MutableStateFlow(null)
    private val isFirstQuestionFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isLastQuestionFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isNextEnabledFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)


    init {
        viewModelScope.launch {
            quizIdFlow.collect { quizId ->
                if (quizId != null) {
                    quizFlow.value = quizRepository.getQuizById(quizId)
                }
            }
        }

        viewModelScope.launch {
            combine(quizFlow, currentQuestionIndexFlow, answersFlow) { quiz, index, answers ->
                Triple(quiz, index, answers)
            }.collect { (quiz, index, answers) ->
                val questions = quiz?.questions ?: emptyList()
                val currentQuestion = questions.getOrNull(index)

                currentQuestionFlow.value = currentQuestion
                isFirstQuestionFlow.value = index == 0
                isLastQuestionFlow.value = (index == questions.size - 1) && questions.isNotEmpty()
                isNextEnabledFlow.value =
                    currentQuestion != null &&
                            answers.containsKey(currentQuestion.id)
            }
        }
    }

    fun onAnswerSelected(questionId: Int, answer: Any?) {
        if (answer == null) {
            Log.e(LOGGING_TAG, "Answer cannot be null")
            return
        }

        answersFlow.value += (questionId to answer)
    }

    fun navigateNext() {
        val quiz = quizFlow.value
        val questions = quiz?.questions

        if(quiz == null) {
            Log.e(LOGGING_TAG, "navigateNext is called with null quiz")
            return
        }
        if(questions.isNullOrEmpty()) {
            Log.e(LOGGING_TAG, "navigateNext is called with empty questions")
            return
        }

        val lastIndex = questions.size - 1
        val currentIndex = currentQuestionIndexFlow.value

        if(currentIndex < lastIndex) {
            currentQuestionIndexFlow.value = currentIndex + 1
        } else if (currentIndex == lastIndex) {
            isFinishedFlow.value = true
        } else {
            Log.e(LOGGING_TAG, "Invalid current question index: $currentIndex")
        }
    }

    fun restartQuiz() {
        isFinishedFlow.value = false
        currentQuestionIndexFlow.value = 0
        answersFlow.value = emptyMap()
    }

    fun navigateBack() {
        if (currentQuestionIndexFlow.value > 0) {
            currentQuestionIndexFlow.value--
        }
    }

    fun uiState(questionId: Int?): QuizUiState {
        val previousQuizId = quizIdFlow.value
        if (previousQuizId != questionId) {
            // If a new quizId is passed in, reset the state. As there isn't a key in the ViewModel creation,
            // there is a chance that the ViewModel might be reused for a different quiz.
            restartQuiz()
            quizIdFlow.value = questionId
        }

        return QuizUiState(
            quizFlow = quizFlow,
            currentQuestionIndexFlow = currentQuestionIndexFlow,
            answersFlow = answersFlow,
            isFinishedFlow = isFinishedFlow,
            currentQuestionFlow = currentQuestionFlow,
            isFirstQuestionFlow = isFirstQuestionFlow,
            isLastQuestionFlow = isLastQuestionFlow,
            isNextEnabledFlow = isNextEnabledFlow,
            onAnswerSelected = ::onAnswerSelected,
            navigateNext = ::navigateNext,
            restartQuiz = ::restartQuiz,
            navigateBack = ::navigateBack
        )
    }
}