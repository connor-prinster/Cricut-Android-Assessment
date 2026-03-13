package com.cricut.androidassessment.ui

import androidx.lifecycle.ViewModel
import com.cricut.androidassessment.enums.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class MainUiState(
    val startRouteFlow: StateFlow<String>
)

@HiltViewModel
class MainViewModel
@Inject constructor() : ViewModel() {
    val startRouteFlow: MutableStateFlow<String> = MutableStateFlow(Routes.ASSESSMENT)

    val uiState = MainUiState(
        startRouteFlow = startRouteFlow
    )
}