package com.cricut.androidassessment.ui.screens.assessment

import android.content.Context
import com.cricut.androidassessment.R
import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.model.Quiz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AssessmentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository: QuizRepository = mock()
    private val mockContext: Context = mock()
    private lateinit var viewModel: AssessmentViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        whenever(mockContext.getString(any())).doReturn("Unknown error occurred")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should start with loading state`() = runTest {
        whenever(mockRepository.getQuizzes()).doReturn(flowOf(emptyList()))

        viewModel = AssessmentViewModel(mockContext, mockRepository)

        assertTrue(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.quizzes.isEmpty())
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `init should fetch quizzes and update uiState`() = runTest {
        val quizzes = listOf(Quiz(1, "Test Quiz", emptyList()))
        whenever(mockRepository.getQuizzes()).doReturn(flowOf(quizzes))

        viewModel = AssessmentViewModel(mockContext, mockRepository)
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals(quizzes, uiState.quizzes)
        assertNull(uiState.error)
        verify(mockRepository).getQuizzes()
    }

    @Test
    fun `init should handle empty quiz list`() = runTest {
        val quizzes = emptyList<Quiz>()
        whenever(mockRepository.getQuizzes()).doReturn(flowOf(quizzes))

        viewModel = AssessmentViewModel(mockContext, mockRepository)
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertEquals(quizzes, uiState.quizzes)
        assertNull(uiState.error)
    }

    @Test
    fun `init should handle error and update uiState with error message`() = runTest {
        val errorMessage = "Network Error"
        whenever(mockRepository.getQuizzes()).doReturn(flow { throw RuntimeException(errorMessage) })

        viewModel = AssessmentViewModel(mockContext, mockRepository)
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertTrue(uiState.quizzes.isEmpty())
        assertEquals(errorMessage, uiState.error)
    }

    @Test
    fun `init should handle error with null message and update uiState with default error message`() = runTest {
        whenever(mockRepository.getQuizzes()).doReturn(flow { throw RuntimeException() })
        whenever(mockContext.getString(R.string.unknown_error_occurred)).doReturn("Default error message")

        viewModel = AssessmentViewModel(mockContext, mockRepository)
        advanceUntilIdle()

        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertTrue(uiState.quizzes.isEmpty())
        assertEquals("Default error message", uiState.error)
    }
}
