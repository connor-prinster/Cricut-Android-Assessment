package com.cricut.androidassessment.ui.screens.quiz

import android.content.Context
import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.model.MultipleChoiceQuestion
import com.cricut.androidassessment.model.Quiz
import com.cricut.androidassessment.model.TrueFalseQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class QuizScreenViewModelTest {
    private val mockRepository: QuizRepository = mock()
    private val mockContext: Context = mock()
    private lateinit var viewModel: QuizScreenViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testQuiz = Quiz(
        id = 1,
        title = "Test Quiz",
        questions = listOf(
            TrueFalseQuestion(1, "Q1", true),
            MultipleChoiceQuestion(2, "Q2", listOf("A", "B"), 0)
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(mockRepository.getQuizById(1)).doReturn(testQuiz)
        viewModel = QuizScreenViewModel(mockContext, mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadQuiz should fetch quiz and update uiState`() = runTest(testDispatcher) {
        viewModel.loadQuiz(1)
        advanceUntilIdle()
        assertEquals(testQuiz, viewModel.uiState.value.quiz)
    }

    @Test
    fun `onAnswerSelected should update answers and isNextEnabled`() =
        runTest(testDispatcher) {
            viewModel.loadQuiz(1)
            advanceUntilIdle()

            viewModel.onAnswerSelected(1, true)
            advanceUntilIdle()

            assertEquals(true, viewModel.uiState.value.answers[1])
            assertTrue(viewModel.uiState.value.isNextEnabled)
        }

    @Test
    fun `navigateNext should increment question index`() = runTest {
        viewModel.loadQuiz(1)
        advanceUntilIdle()

        viewModel.navigateNext()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.currentQuestionIndex)
        assertFalse(viewModel.uiState.value.isFinished)
    }

    @Test
    fun `navigateNext on last question should set isFinished`() = runTest {
        viewModel.loadQuiz(1)
        advanceUntilIdle()

        viewModel.navigateNext()
        viewModel.navigateNext()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isFinished)
    }

    @Test
    fun `isLastQuestion should be true on last question`() = runTest {
        viewModel.loadQuiz(1)
        advanceUntilIdle()

        viewModel.navigateNext()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isLastQuestion)
    }

    @Test
    fun `restartQuiz should reset state`() = runTest {
        viewModel.loadQuiz(1)
        advanceUntilIdle()

        viewModel.onAnswerSelected(1, true)
        viewModel.navigateNext()
        viewModel.restartQuiz()
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertEquals(0, uiState.currentQuestionIndex)
        assertTrue(uiState.answers.isEmpty())
        assertFalse(uiState.isFinished)
    }
}
