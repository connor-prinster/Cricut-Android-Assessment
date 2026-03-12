package com.cricut.androidassessment

import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.ui.AssessmentViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AssessmentViewModelTest {

    private lateinit var viewModel: AssessmentViewModel
    private val repository = QuizRepository()

    @Before
    fun setup() {
        viewModel = AssessmentViewModel(repository)
    }

    @Test
    fun initial_state_is_correct() {
        val uiState = viewModel.uiState.value
        assertEquals(0, uiState.currentQuestionIndex)
        assertEquals(repository.getQuestions().size, uiState.questions.size)
        assertFalse(uiState.isFinished)
    }

    @Test
    fun answering_question_updates_state() {
        val questionId = 1
        val answer = true
        viewModel.onAnswerSelected(questionId, answer)

        assertEquals(answer, viewModel.uiState.value.answers[questionId])
        assertTrue(viewModel.uiState.value.isNextEnabled)
    }

    @Test
    fun navigating_next_updates_current_index() {
        viewModel.onAnswerSelected(1, true)
        viewModel.navigateNext()

        assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
    }

    @Test
    fun navigating_back_preserves_answer() {
        viewModel.onAnswerSelected(1, true)
        viewModel.navigateNext()
        viewModel.navigateBack()

        assertEquals(0, viewModel.uiState.value.currentQuestionIndex)
        assertEquals(true, viewModel.uiState.value.answers[1])
    }
}
