package com.vasilyev.unittests.timer.presentation

import androidx.compose.runtime.Stable

@Stable
data class TimerState(
    val formattedTimerValue: String = "00:00",
    val timerValue: Long = 0L,
    val inputTimerValue: String = "",
    val isRunning: Boolean = false,
    val isPaused: Boolean = false,
    val isInputEmpty: Boolean = false
)


// Dependency Injection
// MVI
// Clean Architecture
// Repository, Dependency Injection, Singleton, State
