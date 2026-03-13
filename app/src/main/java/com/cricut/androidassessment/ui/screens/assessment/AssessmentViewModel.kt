package com.cricut.androidassessment.ui.screens.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.model.Quiz
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssessmentUiState(
    val quizListFlow: StateFlow<List<Quiz>>
) {
    companion object {
        val EMPTY = AssessmentUiState(
            quizListFlow = MutableStateFlow(listOf())
        )
    }
}

@HiltViewModel
class AssessmentViewModel
@Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {
    val quizListFlow: MutableStateFlow<List<Quiz>> = MutableStateFlow(listOf())

    init {
        viewModelScope.launch {
            repository.getQuizzes().collect { quizzes ->
                quizListFlow.value = quizzes
            }
        }
    }


    val uiState = AssessmentUiState(
        quizListFlow = quizListFlow
    )
}
