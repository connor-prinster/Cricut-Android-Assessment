package com.cricut.androidassessment.ui.screens

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cricut.androidassessment.model.MultipleChoiceQuestion
import com.cricut.androidassessment.model.MultipleSelectionQuestion
import com.cricut.androidassessment.model.OpenEndedQuestion
import com.cricut.androidassessment.model.QuizQuestion
import com.cricut.androidassessment.model.TrueFalseQuestion
import com.cricut.androidassessment.ui.AssessmentViewModel
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme

@Composable
fun AssessmentScreen(
    modifier: Modifier = Modifier,
    viewModel: AssessmentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Quiz App Challenge",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (uiState.isFinished) {
            FinishScreen(onRestart = viewModel::restartQuiz)
        } else {
            uiState.currentQuestion?.let { question ->
                QuestionHeader(question = question)
                Spacer(modifier = Modifier.height(24.dp))
                QuestionContent(
                    question = question,
                    selectedAnswer = uiState.answers[question.id],
                    onAnswerSelected = { viewModel.onAnswerSelected(question.id, it) }
                )
                Spacer(modifier = Modifier.weight(1f))
                NavigationButtons(
                    isFirst = uiState.isFirstQuestion,
                    isLast = uiState.isLastQuestion,
                    onNext = viewModel::navigateNext,
                    onBack = viewModel::navigateBack,
                    nextEnabled = uiState.isNextEnabled
                )
            }
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
            text = "Quiz Finished!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRestart) {
            Text("Restart Quiz")
        }
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
            // Optional: Implement other question types or a placeholder
            Text("Question type not implemented yet")
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
            val label = if (answer) "True" else "False"
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
                Text("Back")
            }
        } else {
            Spacer(modifier = Modifier.width(1.dp)) // Maintain space
        }

        Button(
            onClick = onNext,
            enabled = nextEnabled
        ) {
            Text(if (isLast) "Finish" else "Next")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAssessmentScreen() {
    AndroidAssessmentTheme {
        AssessmentScreen()
    }
}
