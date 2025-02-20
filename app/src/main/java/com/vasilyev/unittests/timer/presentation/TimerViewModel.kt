package com.vasilyev.unittests.timer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vasilyev.unittests.timer.domain.usecase.GetTimerValueUseCase
import com.vasilyev.unittests.timer.domain.usecase.SaveTimerValueUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val getTimerValueUseCase: GetTimerValueUseCase,
    private val saveTimerValueUseCase: SaveTimerValueUseCase
): ViewModel() {
    companion object {
        private const val MILLIS_IN_SECOND = 1000
        private const val SECONDS_IN_MINUTE = 60
        private const val ZERO_TIME = 0L
    }

    private val _state = MutableStateFlow(TimerState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeTimerValue()
        }
    }

    fun reduce(intent: TimerIntent) {
        viewModelScope.launch {
            when (intent) {
                is TimerIntent.StartTimer -> {
                    if (state.value.inputTimerValue.isNotEmpty()) {
                        if (isInputValid()) {
                            startNewTimer()
                        } else {
                            clearInput()
                        }
                    } else if (state.value.timerValue == ZERO_TIME) {
                        setIsInputEmpty(true)
                    } else {
                        startTimer()
                    }
                }

                is TimerIntent.PauseTimer -> {
                    if (!state.value.isRunning) return@launch
                    pauseTimer()
                }

                is TimerIntent.UnPauseTimer -> {
                    if (!state.value.isRunning) return@launch
                    unPauseTimer()
                }

                is TimerIntent.StopTimer -> {
                    stopTimer()
                }

                is TimerIntent.ChangeInputTimerValue -> {
                    changeInputTimerValue(intent.newValue)
                }

                is TimerIntent.ResetInputError -> {
                    setIsInputEmpty(false)
                }

                is TimerIntent.OnTick -> {
                    updateTimer(intent.timeLeft)
                }
            }
        }
    }

    private suspend fun updateTimer(time: Long) {
        saveTimerValueUseCase(time)
    }

    private suspend fun startNewTimer() {
        if (!isFieldNotEmpty()) {
            setIsInputEmpty(true)
            return
        }

        updateTimer(state.value.inputTimerValue.toLong() * MILLIS_IN_SECOND)
        startTimer()
    }

    private fun isFieldNotEmpty(): Boolean {
        return state.value.inputTimerValue.isNotEmpty()
    }

    private fun unPauseTimer() {
        startTimer()
        setPaused(false)
    }

    private suspend fun observeTimerValue() {
        getTimerValueUseCase
            .invoke()
            .collect { newValue ->
                _state.update {
                    it.copy(
                        timerValue = newValue,
                        formattedTimerValue = formatTime(newValue)
                    )
                }
            }
    }

    private fun formatTime(timeMillis: Long): String {
        val totalSeconds = timeMillis / MILLIS_IN_SECOND
        val minutes = totalSeconds / SECONDS_IN_MINUTE
        val seconds = totalSeconds % SECONDS_IN_MINUTE

        return String.format("%02d:%02d", minutes, seconds)
    }


    private fun changeInputTimerValue(newValue: String) {
        _state.update {
            it.copy(
                inputTimerValue = newValue
            )
        }
    }

    private fun startTimer() {
        clearInput()
        setRunning(true)
    }

    private fun pauseTimer() {
        setPaused(true)
    }

    private suspend fun stopTimer() {
        updateTimer(ZERO_TIME)
        setRunning(false)
        setPaused(false)
    }


    private fun setRunning(isRunning: Boolean) {
        _state.update {
            it.copy(
                isRunning = isRunning
            )
        }
    }

    private fun setPaused(isPaused: Boolean) {
        _state.update {
            it.copy(
                isPaused = isPaused
            )
        }
    }


    private fun clearInput() {
        _state.update {
            it.copy(
                inputTimerValue = ""
            )
        }
    }

    private fun setIsInputEmpty(value: Boolean) {
        _state.update {
            it.copy(
                isInputEmpty = value
            )
        }
    }

    private fun isInputValid(): Boolean {
        return state.value.inputTimerValue.toLong() > 0
    }
}