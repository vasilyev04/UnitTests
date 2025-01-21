package com.vasilyev.unittests

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.vasilyev.unittests.timer.presentation.TimerScreen
import com.vasilyev.unittests.timer.presentation.TimerViewModel
import com.vasilyev.unittests.ui.theme.UnitTestsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnitTestsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val timerViewModel: TimerViewModel = hiltViewModel()
                    val timerState = timerViewModel.state.collectAsState()

                    TimerScreen(
                        innerPadding = innerPadding,
                        state = timerState.value,
                        onIntent = timerViewModel::reduce
                    )
                }
            }
        }
    }
}