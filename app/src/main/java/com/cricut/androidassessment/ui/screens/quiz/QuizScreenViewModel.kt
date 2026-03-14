package com.cricut.androidassessment.ui.screens.quiz

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricut.androidassessment.R
import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.ext.LOGGING_TAG
import com.cricut.androidassessment.model.Quiz
import com.cricut.androidassessment.model.QuizQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuizUiState(
    val quiz: Quiz? = null,
    val currentQuestionIndex: Int = 0,
    val answers: Map<Int, Any?> = emptyMap(),
    val isFinished: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val currentQuestion: QuizQuestion?
        get() = quiz?.questions?.getOrNull(currentQuestionIndex)

    val isFirstQuestion: Boolean
        get() = currentQuestionIndex == 0

    val isLastQuestion: Boolean
        get() = quiz?.questions?.let { currentQuestionIndex == it.size - 1 } ?: false

    val isNextEnabled: Boolean
        get() {
            val question = currentQuestion ?: return false
            return when (val answer = answers[question.id]) {
                null -> false
                is String -> answer.isNotBlank()
                is Collection<*> -> answer.isNotEmpty()
                else -> true
            }
        }
}

@HiltViewModel
class QuizScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val quizRepository: QuizRepository
) : ViewModel() {
    private val quizIdFlow = MutableStateFlow<Int?>(null)
    private val quizFlow = MutableStateFlow<Quiz?>(null)
    private val currentQuestionIndexFlow = MutableStateFlow(0)
    private val answersFlow = MutableStateFlow<Map<Int, Any?>>(emptyMap())
    private val isFinishedFlow = MutableStateFlow(false)
    private val isLoadingFlow = MutableStateFlow(false)
    private val errorFlow = MutableStateFlow<String?>(null)

    val uiState: StateFlow<QuizUiState> = combine(
        quizFlow,
        currentQuestionIndexFlow,
        answersFlow,
        isFinishedFlow,
        isLoadingFlow,
        errorFlow
    ) { args: Array<*> ->
        QuizUiState(
            quiz = args[0] as? Quiz,
            currentQuestionIndex = args[1] as Int,
            answers = args[2] as Map<Int, Any?>,
            isFinished = args[3] as Boolean,
            isLoading = args[4] as Boolean,
            error = args[5] as? String
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = QuizUiState(isLoading = true)
    )

    init {
        viewModelScope.launch {
            quizIdFlow.collect { quizId ->
                if (quizId != null) {
                    quizFlow.value = null
                    isLoadingFlow.value = true
                    errorFlow.value = null
                    try {
                        val result = quizRepository.getQuizById(quizId)
                        if (result != null) {
                            quizFlow.value = result
                        } else {
                            errorFlow.value = context.getString(R.string.quiz_not_found)
                        }
                    } catch (e: Exception) {
                        Log.e(LOGGING_TAG, "Failed to load quiz with id $quizId", e)
                        errorFlow.value = context.getString(R.string.unknown_error_occurred)
                    } finally {
                        isLoadingFlow.value = false
                    }
                }
            }
        }
    }

    fun loadQuiz(quizId: Int?) {
        if (quizIdFlow.value != quizId) {
            restartQuiz()
            quizIdFlow.value = quizId
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
        val questions = quizFlow.value?.questions ?: return
        val lastIndex = questions.size - 1
        val currentIndex = currentQuestionIndexFlow.value

        if (currentIndex < lastIndex) {
            currentQuestionIndexFlow.value = currentIndex + 1
        } else if (currentIndex == lastIndex) {
            isFinishedFlow.value = true
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
}
