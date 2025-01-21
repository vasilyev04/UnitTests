package com.vasilyev.unittests.timer.presentation

import android.os.CountDownTimer
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.sql.Time


private fun startTimer(
    timeMillis: Long,
    onTick: (Long) -> Unit,
    onFinish: () -> Unit
): CountDownTimer {
    return object : CountDownTimer(timeMillis, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            onTick(millisUntilFinished)
        }

        override fun onFinish() {
            onFinish()
        }
    }.start()
}

@Composable
fun TimerScreen(
    innerPadding: PaddingValues,
    state: TimerState,
    onIntent: (TimerIntent) -> Unit
) {
    val countDownTimer = remember { mutableStateOf<CountDownTimer?>(null) }
    val context = LocalContext.current
    val focusManager: FocusManager = LocalFocusManager.current

    DisposableEffect(Unit) {
        onDispose {
            countDownTimer.value?.cancel()
            countDownTimer.value = null
        }
    }

    LaunchedEffect(state.isInputEmpty) {
        if (state.isInputEmpty) {
            Toast.makeText(
                context,
                "Input is empty",
                Toast.LENGTH_SHORT
            ).show()

            onIntent(TimerIntent.ResetInputError)
        }
    }

    LaunchedEffect(state.isRunning) {
        countDownTimer.value?.cancel()

        if (state.isRunning) {
            countDownTimer.value = startTimer(
                timeMillis = state.timerValue,
                onTick = {
                    onIntent(TimerIntent.OnTick(it))
                },
                onFinish = {
                    onIntent(TimerIntent.StopTimer)
                }
            )
        }
    }

    LaunchedEffect(state.isPaused) {
        countDownTimer.value?.cancel()

        if (!state.isPaused && state.isRunning) {
            countDownTimer.value = startTimer(
                timeMillis = state.timerValue,
                onTick = {
                    onIntent(TimerIntent.OnTick(it))
                },
                onFinish = {
                    onIntent(TimerIntent.StopTimer)
                }
            )
        }
    }

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TimerValueTextField(
            value = state.inputTimerValue,
            onValueChanged = {
                onIntent(TimerIntent.ChangeInputTimerValue(it))
            }
        )

        Spacer(modifier = Modifier.height(60.dp))

        TimerText(state.formattedTimerValue)

        Spacer(modifier = Modifier.height(60.dp))

        TimerButtons(
            isRunning = state.isRunning,
            isPaused = state.isPaused,
            onPauseTimer = {
                onIntent(TimerIntent.PauseTimer)
                focusManager.clearFocus()
            },
            onUnPauseTimer = {
                onIntent(TimerIntent.UnPauseTimer)
                focusManager.clearFocus()
            },
            onStartTimer = {
                onIntent(TimerIntent.StartTimer)
                focusManager.clearFocus()
            },
            onStopTimer = {
                onIntent(TimerIntent.StopTimer)
                focusManager.clearFocus()
            }
        )
    }
}

@Composable
private fun TimerValueTextField(
    value: String,
    onValueChanged: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChanged,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        label = {
            Text(text = "Enter time in seconds")
        }
    )
}

@Composable
private fun TimerText(
    value: String
) {
    Text(
        text = value,
        fontSize = 38.sp,
    )
}


@Composable
private fun TimerButtons(
    isRunning: Boolean,
    isPaused: Boolean,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onUnPauseTimer: () -> Unit,
    onStopTimer: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = if (isRunning) onStopTimer else onStartTimer,
            content = {
                if (isRunning) {
                    Text(text = "Stop")
                } else {
                    Text(text = "Start")
                }
            }
        )

        Button(
            onClick = if (isPaused) onUnPauseTimer else onPauseTimer,
            content = {
                if (isPaused) {
                    Text(text = "Unpause")
                } else {
                    Text(text = "Pause")
                }
            }
        )
    }
}

@Preview
@Composable
private fun Preview() {
    TimerScreen(
        innerPadding = PaddingValues(),
        state = TimerState(),
        onIntent = {}
    )
}