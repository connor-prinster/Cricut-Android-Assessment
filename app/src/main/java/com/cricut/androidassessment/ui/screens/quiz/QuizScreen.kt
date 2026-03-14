package com.cricut.androidassessment.ui.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cricut.androidassessment.R
import com.cricut.androidassessment.model.MultipleChoiceQuestion
import com.cricut.androidassessment.model.MultipleSelectionQuestion
import com.cricut.androidassessment.model.OpenEndedQuestion
import com.cricut.androidassessment.model.Quiz
import com.cricut.androidassessment.model.QuizQuestion
import com.cricut.androidassessment.model.TrueFalseQuestion
import com.cricut.androidassessment.ui.components.MultipleChoiceContent
import com.cricut.androidassessment.ui.components.MultipleSelectionContent
import com.cricut.androidassessment.ui.components.OpenEndedContent
import com.cricut.androidassessment.ui.components.PaddedScaffold
import com.cricut.androidassessment.ui.components.TrueFalseContent
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun QuizScreen(
    navController: NavController,
    viewModel: QuizScreenViewModel = hiltViewModel(),
    quizId: Int?,
) {
    // use remember so we don't recreate a uiState over and over again if no quizId changes
    val uiState = remember(quizId) { viewModel.uiState(quizId) }
    val nullableQuiz by uiState.quizFlow.collectAsState()
    // as it's delegated, quiz can be null
    val quiz = nullableQuiz
    val isFirst by uiState.isFirstQuestionFlow.collectAsState()
    val isLast by uiState.isLastQuestionFlow.collectAsState()
    val isFinished by uiState.isFinishedFlow.collectAsState()

    val nextEnabled by uiState.isNextEnabledFlow.collectAsState()

    PaddedScaffold(
        bottomBar = {
            if (quiz != null && !isFinished) {
                NavigationButtons(
                    isFirst = isFirst,
                    isLast = isLast,
                    onNext = { uiState.navigateNext() },
                    onBack = { uiState.navigateBack() },
                    nextEnabled = nextEnabled
                )
            } else if (isFinished) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp, horizontal = 24.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = { navController.popBackStack() }) {
                        Text(stringResource(R.string.main_menu))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = uiState.restartQuiz) {
                        Text(stringResource(R.string.restart_quiz))
                    }
                }
            }
        }
    ) {
        QuizScreenContent(uiState = uiState, quiz = quiz)
    }
}

@Composable
private fun QuizScreenContent(uiState: QuizUiState, quiz: Quiz?) {
    if (quiz != null) {
        val isFinished by uiState.isFinishedFlow.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = quiz.title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            if (isFinished) {
                FinishScreen(uiState)
            } else {
                QuizInProgress(uiState = uiState, quiz = quiz)
            }
        }
    } else {
        NoValidQuiz()
    }
}

@Composable
private fun QuizInProgress(uiState: QuizUiState, quiz: Quiz) {
    val currentQuestionIndex by uiState.currentQuestionIndexFlow.collectAsState()
    val currentQuestion = quiz.questions[currentQuestionIndex]

    val answers by uiState.answersFlow.collectAsState()
    val selectedAnswer = answers[currentQuestion.id]

    val scrollState = rememberScrollState()
    LaunchedEffect(currentQuestion.id) {
        scrollState.scrollTo(0)
    }

    val totalQuestions = quiz.questions.size
    val progress = (currentQuestionIndex + 1).toFloat() / totalQuestions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        )
        QuestionHeader(
            question = currentQuestion,
            index = currentQuestionIndex + 1,
            total = totalQuestions
        )
        Spacer(modifier = Modifier.height(24.dp))
        QuestionContent(
            question = currentQuestion,
            selectedAnswer = selectedAnswer,
            onAnswerSelected = { uiState.onAnswerSelected(currentQuestion.id, it) }
        )
    }
}

@Composable
private fun NoValidQuiz() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(R.string.no_valid_quiz))
    }
}

@Composable
private fun QuestionHeader(question: QuizQuestion, index: Int, total: Int) {
    Column {
        Text(
            text = stringResource(R.string.question_x_of_y, index, total),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = question.questionText,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
private fun QuestionContent(
    question: QuizQuestion,
    selectedAnswer: Any?,
    showAnswers: Boolean = false,
    onAnswerSelected: (Any) -> Unit
) {
    when (question) {
        is TrueFalseQuestion -> TrueFalseContent(
            question = question,
            selectedAnswer = selectedAnswer as? Boolean,
            showAnswers = showAnswers,
            onAnswerSelected = onAnswerSelected
        )

        is MultipleChoiceQuestion -> MultipleChoiceContent(
            question = question,
            selectedAnswerIndex = selectedAnswer as? Int,
            showAnswers = showAnswers,
            onAnswerSelected = onAnswerSelected
        )

        is MultipleSelectionQuestion -> MultipleSelectionContent(
            question = question,
            selectedAnswerIndices = (selectedAnswer as? Set<*>)?.filterIsInstance<Int>()?.toSet(),
            showAnswers = showAnswers,
            onAnswerSelected = onAnswerSelected
        )

        is OpenEndedQuestion -> OpenEndedContent(
            question = question,
            selectedAnswer = selectedAnswer as? String,
            showAnswers = showAnswers,
            onAnswerSelected = onAnswerSelected
        )
    }
}

@Composable
private fun NavigationButtons(
    isFirst: Boolean,
    isLast: Boolean,
    onNext: () -> Unit,
    onBack: () -> Unit,
    nextEnabled: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 30.dp, horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (!isFirst) {
            OutlinedButton(onClick = onBack) {
                Text(stringResource(R.string.back))
            }
        } else {
            Spacer(modifier = Modifier.width(1.dp))
        }

        Button(
            onClick = onNext,
            enabled = nextEnabled
        ) {
            Text(stringResource(if (isLast) R.string.finish else R.string.next))
        }
    }
}

@Composable
private fun FinishScreen(uiState: QuizUiState) {
    val quiz by uiState.quizFlow.collectAsState()
    val questions = quiz?.questions
    val answers by uiState.answersFlow.collectAsState()

    if (questions != null) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.quiz_finished),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                itemsIndexed(questions) { index, question ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    ) {
                        QuestionHeader(
                            question = question,
                            index = index + 1,
                            total = questions.size
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        QuestionContent(
                            question = question,
                            selectedAnswer = answers[question.id],
                            showAnswers = true,
                            onAnswerSelected = {
                                // do nothing
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuizScreenEmpty() {
    AndroidAssessmentTheme {
        QuizScreenContent(uiState = QuizUiState.EMPTY, quiz = null)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuizScreenPopulated() {
    AndroidAssessmentTheme {
        QuizScreenContent(
            uiState = QuizUiState.EMPTY.copy(
                quizFlow = MutableStateFlow(Quiz.generateTest())
            ),
            quiz = Quiz.generateTest()
        )
    }
}