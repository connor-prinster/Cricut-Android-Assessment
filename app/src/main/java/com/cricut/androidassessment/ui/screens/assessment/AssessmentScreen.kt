package com.cricut.androidassessment.ui.screens.assessment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cricut.androidassessment.R
import com.cricut.androidassessment.enums.Routes
import com.cricut.androidassessment.model.Quiz
import com.cricut.androidassessment.ui.components.PaddedScaffold
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme

@Composable
fun AssessmentScreen(
    navController: NavController,
    viewModel: AssessmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    PaddedScaffold {
        AssessmentScreenContent(uiState = uiState) { route ->
            navController.navigate(route)
        }
    }
}

@Composable
private fun AssessmentScreenContent(uiState: AssessmentUiState, onNav: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.available_quizzes),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            uiState.quizzes.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = stringResource(R.string.no_quizzes_found))
                }
            }

            else -> {
                LazyColumn(modifier = Modifier.weight(1F)) {
                    items(uiState.quizzes) { quiz ->
                        QuizRow(quiz = quiz) {
                            onNav(Routes.generateQuizRoute(quiz.id))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizRow(quiz: Quiz, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = true,
                onClickLabel = stringResource(R.string.navigate_to_quiz),
                onClick = onClick
            )
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = quiz.title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.arrow_forward),
            contentDescription = null
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewAssessmentScreenNoQuizzes() {
    AndroidAssessmentTheme {
        AssessmentScreenContent(uiState = AssessmentUiState()) {

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAssessmentScreenLoading() {
    AndroidAssessmentTheme {
        AssessmentScreenContent(uiState = AssessmentUiState(isLoading = true)) {

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAssessmentScreenQuizzesPresent() {
    AndroidAssessmentTheme {
        AssessmentScreenContent(
            uiState = AssessmentUiState(
                quizzes = listOf(
                    Quiz.generateTest(), Quiz.generateTest()
                )
            )
        ) {

        }
    }
}
