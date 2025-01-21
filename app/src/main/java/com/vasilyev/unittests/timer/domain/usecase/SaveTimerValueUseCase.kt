package com.vasilyev.unittests.timer.domain.usecase

import com.vasilyev.unittests.timer.domain.repository.TimerRepository
import javax.inject.Inject

class SaveTimerValueUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {

    suspend operator fun invoke(timerValue: Long) {
        timerRepository.saveTimerValue(timerValue)
    }
}