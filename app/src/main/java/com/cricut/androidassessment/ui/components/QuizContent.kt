package com.cricut.androidassessment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cricut.androidassessment.R
import com.cricut.androidassessment.model.MultipleChoiceQuestion
import com.cricut.androidassessment.model.MultipleSelectionQuestion
import com.cricut.androidassessment.model.OpenEndedQuestion
import com.cricut.androidassessment.model.TrueFalseQuestion
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme
import com.cricut.androidassessment.ui.theme.CorrectGreen
import com.cricut.androidassessment.ui.theme.IncorrectRed

@Composable
fun TrueFalseContent(
    question: TrueFalseQuestion,
    selectedAnswer: Boolean?,
    showAnswers: Boolean,
    onAnswerSelected: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        listOf(true, false).forEach { answer ->
            val label =
                stringResource(if (answer) R.string.true_label else R.string.false_label)
            val isSelected = selectedAnswer == answer
            val isCorrect = answer == question.correctAnswer
            val backgroundColor = when {
                showAnswers && isCorrect -> CorrectGreen.copy(alpha = 0.1f)
                showAnswers && isSelected -> IncorrectRed.copy(alpha = 0.1f)
                else -> Color.Transparent
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(backgroundColor)
                    .selectable(
                        selected = isSelected,
                        onClick = { onAnswerSelected(answer) },
                        role = Role.RadioButton,
                        enabled = !showAnswers
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null // null because it's handled by Row selectable
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }
    }
}

@Composable
fun MultipleChoiceContent(
    question: MultipleChoiceQuestion,
    selectedAnswerIndex: Int?,
    showAnswers: Boolean,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier.selectableGroup()
    ) {
        question.options.forEachIndexed { index, option ->
            val isSelected = selectedAnswerIndex == index
            val isCorrect = index == question.correctAnswerIndex
            val backgroundColor = when {
                showAnswers && isCorrect -> CorrectGreen.copy(alpha = 0.1f)
                showAnswers && isSelected -> IncorrectRed.copy(alpha = 0.1f)
                else -> Color.Transparent
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(backgroundColor)
                    .selectable(
                        selected = isSelected,
                        onClick = { onAnswerSelected(index) },
                        role = Role.RadioButton,
                        enabled = !showAnswers
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = null
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }
    }
}

@Composable
fun MultipleSelectionContent(
    question: MultipleSelectionQuestion,
    selectedAnswerIndices: Set<Int>?,
    showAnswers: Boolean,
    onAnswerSelected: (Set<Int>) -> Unit
) {
    val selectedIndices = selectedAnswerIndices ?: emptySet()

    Column {
        question.options.forEachIndexed { index, option ->
            val isSelected = selectedIndices.contains(index)
            val isCorrect = question.correctAnswerIndices.contains(index)
            val backgroundColor = when {
                showAnswers && isCorrect -> CorrectGreen.copy(alpha = 0.1f)
                showAnswers && isSelected -> IncorrectRed.copy(alpha = 0.1f)
                else -> Color.Transparent
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(backgroundColor)
                    .toggleable(
                        value = isSelected,
                        onValueChange = { checked ->
                            val newSelection = if (checked) {
                                selectedIndices + index
                            } else {
                                selectedIndices - index
                            }
                            onAnswerSelected(newSelection)
                        },
                        role = Role.Checkbox,
                        enabled = !showAnswers
                    )
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = null
                )
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }
    }
}

@Composable
fun OpenEndedContent(
    question: OpenEndedQuestion,
    selectedAnswer: String?,
    showAnswers: Boolean,
    onAnswerSelected: (String) -> Unit
) {
    val isCorrect =
        selectedAnswer?.trim()?.equals(question.correctAnswer.trim(), ignoreCase = true) == true
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = selectedAnswer ?: "",
            onValueChange = onAnswerSelected,
            label = { Text(stringResource(R.string.open_ended_label)) },
            placeholder = { Text(stringResource(R.string.open_ended_hint)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 5,
            enabled = !showAnswers,
            isError = showAnswers && !isCorrect
        )

        if (showAnswers) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isCorrect) "Correct!" else "Correct answer: ${question.correctAnswer}",
                color = if (isCorrect) CorrectGreen else IncorrectRed,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrueFalseContentPreview() {
    AndroidAssessmentTheme {
        TrueFalseContent(
            question = TrueFalseQuestion(1, "Question", true),
            selectedAnswer = true,
            showAnswers = false,
            onAnswerSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrueFalseContentPreviewCorrectAnswer() {
    AndroidAssessmentTheme {
        TrueFalseContent(
            question = TrueFalseQuestion(1, "Question", true),
            selectedAnswer = true,
            showAnswers = true,
            onAnswerSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MultipleChoiceContentPreview() {
    AndroidAssessmentTheme {
        MultipleChoiceContent(
            question = MultipleChoiceQuestion(
                id = 1,
                questionText = "Select one of the choices below",
                options = listOf("Choice 1", "Choice 2", "Choice 3"),
                correctAnswerIndex = 2
            ),
            selectedAnswerIndex = 0,
            showAnswers = false,
            onAnswerSelected = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MultipleChoiceContentPreviewCorrectAnswer() {
    AndroidAssessmentTheme {
        MultipleChoiceContent(
            question = MultipleChoiceQuestion(
                id = 1,
                questionText = "Select one of the choices below",
                options = listOf("Choice 1", "Choice 2", "Choice 3"),
                correctAnswerIndex = 2
            ),
            selectedAnswerIndex = 0,
            showAnswers = true,
            onAnswerSelected = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMultipleSelectionContent() {
    AndroidAssessmentTheme {
        MultipleSelectionContent(
            question = MultipleSelectionQuestion(
                id = 1,
                questionText = "There are multiple correct answers",
                options = listOf("Correct", "Wrong", "Right", "Incorrect"),
                correctAnswerIndices = setOf(1, 3)
            ),
            selectedAnswerIndices = setOf(1, 2),
            showAnswers = false,
            onAnswerSelected = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMultipleSelectionContentCorrectAnswer() {
    AndroidAssessmentTheme {
        MultipleSelectionContent(
            question = MultipleSelectionQuestion(
                id = 1,
                questionText = "There are multiple correct answers",
                options = listOf("Correct", "Wrong", "Right", "Incorrect"),
                correctAnswerIndices = setOf(1, 3)
            ),
            selectedAnswerIndices = setOf(1, 2),
            showAnswers = true,
            onAnswerSelected = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOpenEndedContent() {
    AndroidAssessmentTheme {
        OpenEndedContent(
            question = OpenEndedQuestion(1, "Question", "Answer"),
            selectedAnswer = "",
            showAnswers = false,
            onAnswerSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOpenEndedContentCorrectAnswer() {
    AndroidAssessmentTheme {
        OpenEndedContent(
            question = OpenEndedQuestion(1, "Question", "Answer"),
            selectedAnswer = "This is a correct answer",
            showAnswers = true,
            onAnswerSelected = {}
        )
    }
}
