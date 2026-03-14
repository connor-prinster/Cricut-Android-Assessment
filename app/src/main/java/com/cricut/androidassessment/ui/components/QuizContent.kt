package com.cricut.androidassessment.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cricut.androidassessment.R
import com.cricut.androidassessment.model.MultipleChoiceQuestion
import com.cricut.androidassessment.model.MultipleSelectionQuestion
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme

@Composable
fun TrueFalseContent(
    selectedAnswer: Boolean?,
    onAnswerSelected: (Boolean) -> Unit
) {
    val listState = rememberLazyListState()
    Box {
        LazyColumn(
            state = listState,
            modifier = Modifier.selectableGroup()
        ) {
            items(listOf(true, false)) { answer ->
                val label =
                    stringResource(if (answer) R.string.true_label else R.string.false_label)
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

        ListAnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            listState = listState
        )
    }
}

@Composable
fun MultipleChoiceContent(
    question: MultipleChoiceQuestion,
    selectedAnswerIndex: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState()
    Box {
        LazyColumn(
            state = listState,
            modifier = Modifier.selectableGroup()
        ) {
            itemsIndexed(question.options) { index, option ->
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

        ListAnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            listState = listState
        )
    }
}

@Composable
fun MultipleSelectionContent(
    question: MultipleSelectionQuestion,
    selectedAnswerIndices: Set<Int>?,
    onAnswerSelected: (Set<Int>) -> Unit
) {
    val listState = rememberLazyListState()
    val selectedIndices = selectedAnswerIndices ?: emptySet()

    Box {
        LazyColumn(
            state = listState
        ) {
            itemsIndexed(question.options) { index, option ->
                val isSelected = selectedIndices.contains(index)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
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
                            role = Role.Checkbox
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
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        ListAnimatedVisibility(
            modifier = Modifier.align(Alignment.BottomCenter),
            listState = listState
        )
    }
}

@Composable
fun OpenEndedContent(
    selectedAnswer: String?,
    onAnswerSelected: (String) -> Unit
) {
    Box(
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
            maxLines = 5
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrueFalseContentPreview() {
    AndroidAssessmentTheme {
        TrueFalseContent(selectedAnswer = true, onAnswerSelected = {})
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
            onAnswerSelected = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewOpenEndedContent() {
    AndroidAssessmentTheme {
        OpenEndedContent(selectedAnswer = "", onAnswerSelected = {})
    }
}
