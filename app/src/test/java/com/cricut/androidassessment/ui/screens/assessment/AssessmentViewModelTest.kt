package com.cricut.androidassessment.ui.screens.assessment

import com.cricut.androidassessment.data.QuizRepository
import com.cricut.androidassessment.model.Quiz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AssessmentViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val repository: QuizRepository = mock()
    private lateinit var viewModel: AssessmentViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should fetch quizzes and update quizListFlow`() = runTest {
        val quizzes = listOf(Quiz(1, "Test Quiz", emptyList()))
        whenever(repository.getQuizzes()).doReturn(flowOf(quizzes))

        viewModel = AssessmentViewModel(repository)
        advanceUntilIdle()

        assertEquals(quizzes, viewModel.quizListFlow.value)
        verify(repository).getQuizzes()
    }
}
