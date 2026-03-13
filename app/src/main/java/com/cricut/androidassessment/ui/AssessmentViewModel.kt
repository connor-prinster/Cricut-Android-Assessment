package com.cricut.androidassessment.ui

import androidx.lifecycle.ViewModel
import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.model.QuizQuestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class QuizUiState(
    val currentQuestionIndex: Int = 0,
    val questions: List<QuizQuestion> = emptyList(),
    val answers: Map<Int, Any?> = emptyMap(),
    val isFinished: Boolean = false
) {
    val currentQuestion: QuizQuestion?
        get() = if (isFinished) null else questions.getOrNull(currentQuestionIndex)

    val isFirstQuestion: Boolean
        get() = currentQuestionIndex == 0 && !isFinished

    val isLastQuestion: Boolean
        get() = currentQuestionIndex == questions.size - 1

    val isNextEnabled: Boolean
        get() = answers.containsKey(currentQuestion?.id)
}

@HiltViewModel
class AssessmentViewModel
@Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        QuizUiState(
            questions = repository.getQuestions()
        )
    )
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun onAnswerSelected(questionId: Int, answer: Any?) {
        _uiState.update { currentState ->
            currentState.copy(
                answers = currentState.answers + (questionId to answer)
            )
        }
    }

    fun navigateNext() {
        _uiState.update { currentState ->
            if (currentState.currentQuestionIndex < currentState.questions.size - 1) {
                currentState.copy(currentQuestionIndex = currentState.currentQuestionIndex + 1)
            } else {
                currentState.copy(isFinished = true)
            }
        }
    }

    fun restartQuiz() {
        _uiState.update { currentState ->
            currentState.copy(
                currentQuestionIndex = 0,
                answers = emptyMap(),
                isFinished = false
            )
        }
    }

    fun navigateBack() {
        _uiState.update { currentState ->
            if (currentState.currentQuestionIndex > 0) {
                currentState.copy(currentQuestionIndex = currentState.currentQuestionIndex - 1)
            } else {
                currentState
            }
        }
    }
}
