package com.cricut.androidassessment.ui.screens.assessment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cricut.androidassessment.R
import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.model.Quiz
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AssessmentUiState(
    val quizzes: List<Quiz> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AssessmentViewModel @Inject constructor(
    @ApplicationContext private val context: android.content.Context,
    repository: QuizRepository
) : ViewModel() {

    val uiState: StateFlow<AssessmentUiState> = repository.getQuizzes()
        .map { quizzes ->
            AssessmentUiState(quizzes = quizzes, isLoading = false)
        }
        .onStart {
            emit(AssessmentUiState(isLoading = true))
        }
        .catch { e ->
            emit(
                AssessmentUiState(
                    error = e.message ?: context.getString(R.string.unknown_error_occurred),
                    isLoading = false
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AssessmentUiState(isLoading = true)
        )
}
