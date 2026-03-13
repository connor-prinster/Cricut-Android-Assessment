package com.cricut.androidassessment.ui

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cricut.androidassessment.ext.stripCurlies
import com.cricut.androidassessment.enums.NavigationConstants.NAV_ARG_QUIZ_ID
import com.cricut.androidassessment.enums.Routes
import com.cricut.androidassessment.ui.screens.assessment.AssessmentScreen
import com.cricut.androidassessment.ui.screens.question.QuizScreen

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
            arguments = listOf(navArgument(NAV_ARG_QUIZ_ID) { type = NavType.StringType })
        ) { backstackEntry ->
            val quizId = backstackEntry.arguments?.getString(NAV_ARG_QUIZ_ID)?.stripCurlies()
                ?.let { encodedId ->
                    Uri.decode(encodedId)
                }?.toIntOrNull()

            QuizScreen(quizId = quizId)
        }
    }
}