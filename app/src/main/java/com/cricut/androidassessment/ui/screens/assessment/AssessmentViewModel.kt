package com.cricut.androidassessment.ui.screens.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.model.Quiz
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _quizListFlow: MutableStateFlow<List<Quiz>> = MutableStateFlow(listOf())
    val quizListFlow: StateFlow<List<Quiz>> = _quizListFlow.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getQuizzes().collect { quizzes ->
                _quizListFlow.value = quizzes
            }
        }
    }


    val uiState = AssessmentUiState(
        quizListFlow = quizListFlow
    )
}
