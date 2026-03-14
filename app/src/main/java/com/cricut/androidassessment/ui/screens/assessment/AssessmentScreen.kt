package com.cricut.androidassessment.ui.screens.assessment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.cricut.androidassessment.R
import com.cricut.androidassessment.model.Quiz
import com.cricut.androidassessment.enums.Routes
import com.cricut.androidassessment.ui.theme.AndroidAssessmentTheme
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun AssessmentScreen(
    navController: NavController,
    viewModel: AssessmentViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            AssessmentScreenContent(uiState = uiState) { route ->
                navController.navigate(route)
            }
        }
    }
}

@Composable
private fun AssessmentScreenContent(uiState: AssessmentUiState, onNav: (String) -> Unit) {
    val quizList by uiState.quizListFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.quiz_app_challenge),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (quizList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = stringResource(R.string.no_quizzes_found))
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1F)) {
                items(quizList) { quiz ->
                    QuizRow(quiz = quiz) {
                        onNav(Routes.generateQuizRoute(quiz.id))
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
    ) {
        Text(
            text = quiz.title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(R.drawable.arrow_forward),
                contentDescription = stringResource(R.string.navigate_to_quiz)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewAssessmentScreenNoQuizzes() {
    AndroidAssessmentTheme {
        AssessmentScreenContent(uiState = AssessmentUiState.EMPTY) {

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAssessmentScreenQuizzesPresent() {
    AndroidAssessmentTheme {
        AssessmentScreenContent(
            uiState = AssessmentUiState.EMPTY.copy(
                quizListFlow = MutableStateFlow(
                    listOf(
                        Quiz.generateTest(), Quiz.generateTest()
                    )
                )
            )
        ) {

        }
    }
}
