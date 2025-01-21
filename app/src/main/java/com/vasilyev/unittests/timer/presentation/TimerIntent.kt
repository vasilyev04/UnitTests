package com.vasilyev.unittests.timer.presentation

sealed interface TimerIntent {
    data object StartTimer: TimerIntent
    data object PauseTimer: TimerIntent
    data object UnPauseTimer: TimerIntent
    data object StopTimer: TimerIntent
    data object ResetInputError: TimerIntent
    data class ChangeInputTimerValue(val newValue: String): TimerIntent
    data class OnTick(val timeLeft: Long): TimerIntent
}