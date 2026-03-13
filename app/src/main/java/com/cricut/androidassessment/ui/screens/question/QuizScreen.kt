package com.cricut.androidassessment.ui.screens.question

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cricut.androidassessment.R
import com.cricut.androidassessment.model.MultipleChoiceQuestion
import com.cricut.androidassessment.model.Quiz
import com.cricut.androidassessment.model.QuizQuestion
import com.cricut.androidassessment.model.TrueFalseQuestion
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun QuizScreen(
    viewModel: QuizScreenViewModel = hiltViewModel(),
    quizId: Int?,
) {
    val uiState = remember(quizId) { viewModel.uiState(quizId) }
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            QuizScreenContent(uiState = uiState)
        }
    }
}

@Composable
private fun QuizScreenContent(uiState: QuizUiState) {
    val nullableQuiz by uiState.quizFlow.collectAsState()
    val quiz = nullableQuiz
    if (quiz != null) {
        val isFinished by uiState.isFinishedFlow.collectAsState()
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)) {
            Text(
                text = quiz.title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            if (isFinished) {
                FinishScreen { uiState.restartQuiz() }
            } else {
                val currentQuestionIndex by uiState.currentQuestionIndexFlow.collectAsState()
                val currentQuestion = quiz.questions[currentQuestionIndex]

                val answers by uiState.answersFlow.collectAsState()
                val selectedAnswer = answers[currentQuestion.id]

                val isFirst by uiState.isFirstQuestionFlow.collectAsState()
                val isLast by uiState.isLastQuestionFlow.collectAsState()

                val nextEnabled by uiState.isNextEnabledFlow.collectAsState()

                QuestionHeader(question = currentQuestion)
                Spacer(modifier = Modifier.height(24.dp))
                QuestionContent(
                    question = currentQuestion,
                    selectedAnswer = selectedAnswer,
                    onAnswerSelected = { uiState.onAnswerSelected(currentQuestion.id, it) }
                )
                Spacer(modifier = Modifier.weight(1f))
                NavigationButtons(
                    isFirst = isFirst,
                    isLast = isLast,
                    onNext = { uiState.navigateNext() },
                    onBack = { uiState.navigateBack() },
                    nextEnabled = nextEnabled
                )
            }
        }
    } else {
        NoValidQuiz()
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
private fun QuestionHeader(question: QuizQuestion) {
    Column {
        Text(
            text = "Question ${question.id}",
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
    onAnswerSelected: (Any) -> Unit
) {
    when (question) {
        is TrueFalseQuestion -> TrueFalseContent(
            selectedAnswer = selectedAnswer as? Boolean,
            onAnswerSelected = onAnswerSelected
        )

        is MultipleChoiceQuestion -> MultipleChoiceContent(
            question = question,
            selectedAnswerIndex = selectedAnswer as? Int,
            onAnswerSelected = onAnswerSelected
        )

        else -> {
            Text(stringResource(R.string.question_type_not_implemented))
        }
    }
}

@Composable
private fun TrueFalseContent(
    selectedAnswer: Boolean?,
    onAnswerSelected: (Boolean) -> Unit
) {
    Column(modifier = Modifier.selectableGroup()) {
        listOf(true, false).forEach { answer ->
            val label = stringResource(if (answer) R.string.true_label else R.string.false_label)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (selectedAnswer == answer),
                        onClick = { onAnswerSelected(answer) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectedAnswer == answer),
                    onClick = null // null because it's handled by Row selectable
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun MultipleChoiceContent(
    question: MultipleChoiceQuestion,
    selectedAnswerIndex: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    Column(modifier = Modifier.selectableGroup()) {
        question.options.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .selectable(
                        selected = (selectedAnswerIndex == index),
                        onClick = { onAnswerSelected(index) },
                        role = Role.RadioButton
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (selectedAnswerIndex == index),
                    onClick = null
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
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
        modifier = Modifier.fillMaxWidth(),
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
private fun FinishScreen(onRestart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.quiz_finished),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRestart) {
            Text(stringResource(R.string.restart_quiz))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuizScreenEmpty() {
    MaterialTheme {
        QuizScreenContent(uiState = QuizUiState.EMPTY)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuizScreenPopulated() {
    MaterialTheme {
        QuizScreenContent(
            uiState = QuizUiState.EMPTY.copy(
                quizFlow = MutableStateFlow(Quiz.generateTest())
            )
        )
    }
}