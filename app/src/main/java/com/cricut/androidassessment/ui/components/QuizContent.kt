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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.cricut.androidassessment.R
import com.cricut.androidassessment.model.MultipleChoiceQuestion

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
