package com.cricut.androidassessment.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cricut.androidassessment.enums.NavigationConstants.NAV_ARG_QUIZ_ID
import com.cricut.androidassessment.enums.Routes
import com.cricut.androidassessment.ui.screens.assessment.AssessmentScreen
import com.cricut.androidassessment.ui.screens.quiz.QuizScreen

@Composable
fun MainNavHost(navController: NavHostController) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val uiState = mainViewModel.uiState
    val startRoute by uiState.startRouteFlow.collectAsState()

    NavHost(navController = navController, startDestination = startRoute) {
        composable(route = Routes.ASSESSMENT) {
            AssessmentScreen(navController = navController)
        }

        composable(
            route = Routes.QUIZ,
            arguments = listOf(navArgument(NAV_ARG_QUIZ_ID) { type = NavType.IntType })
        ) { backstackEntry ->
            val quizId = backstackEntry.arguments?.getInt(NAV_ARG_QUIZ_ID)

            QuizScreen(navController = navController, quizId = quizId)
        }
    }
}
