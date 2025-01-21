package com.vasilyev.unittests.timer.domain.usecase

import com.vasilyev.unittests.timer.domain.repository.TimerRepository
import javax.inject.Inject

class GetTimerValueUseCase @Inject constructor(
    private val timerRepository: TimerRepository
) {

    operator fun invoke() = timerRepository.getTimerValue()
}