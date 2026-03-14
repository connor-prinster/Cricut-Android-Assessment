package com.cricut.androidassessment.ui.screens.question

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
    private val repository: QuizRepository = mock()
    private lateinit var viewModel: QuizScreenViewModel

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
        whenever(repository.getQuizById(1)).doReturn(testQuiz)
        viewModel = QuizScreenViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `uiState should set quizId and fetch quiz`() = runTest {
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        try {
            viewModel.uiState(1)
            advanceUntilIdle()
            assertEquals(testQuiz, viewModel.uiState(1).quizFlow.value)
        } finally {
            Dispatchers.resetMain()
        }

    }

    @Test
    fun `onAnswerSelected should update answersFlow and isNextEnabledFlow`() = runTest {
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        try {
            viewModel.uiState(1)
            advanceUntilIdle()

            viewModel.onAnswerSelected(1, true)
            advanceUntilIdle()

            assertEquals(true, viewModel.uiState(1).answersFlow.value[1])
            assertTrue(viewModel.uiState(1).isNextEnabledFlow.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `navigateNext should increment question index`() = runTest {
        val testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        try {
            viewModel.uiState(1)
            advanceUntilIdle()

            viewModel.navigateNext()
            advanceUntilIdle()

            assertEquals(1, viewModel.uiState(1).currentQuestionIndexFlow.value)
            assertFalse(viewModel.uiState(1).isFinishedFlow.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `navigateNext on last question should set isFinished`() = runTest {
        try {
            viewModel.uiState(1)
            advanceUntilIdle()

            viewModel.navigateNext()
            viewModel.navigateNext()
            advanceUntilIdle()

            assertTrue(viewModel.uiState(1).isFinishedFlow.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `isLastQuestionFlow should be true on last question`() = runTest {
        try {
            viewModel.uiState(1)
            advanceUntilIdle()

            viewModel.navigateNext()
            advanceUntilIdle()

            assertTrue(viewModel.uiState(1).isLastQuestionFlow.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `restartQuiz should reset state`() = runTest {
        try {
            viewModel.uiState(1)
            advanceUntilIdle()

            viewModel.onAnswerSelected(1, true)
            viewModel.navigateNext()
            viewModel.restartQuiz()
            advanceUntilIdle()

            val uiState = viewModel.uiState(1)
            assertEquals(0, uiState.currentQuestionIndexFlow.value)
            assertTrue(uiState.answersFlow.value.isEmpty())
            assertFalse(uiState.isFinishedFlow.value)
        } finally {
            Dispatchers.resetMain()
        }
    }
}
