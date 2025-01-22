package com.vasilyev.unittests.timer.domain.usecase

import com.vasilyev.unittests.timer.domain.repository.TimerRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SaveTimerValueUseCaseTest {
    private val testDispatcher = StandardTestDispatcher()
    private val timerRepository: TimerRepository = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `verify that repository save timer value method calls`() = runTest {
        val useCase = SaveTimerValueUseCase(timerRepository)

        useCase.invoke(6000L)

        coVerify(exactly = 1) { timerRepository.saveTimerValue(any()) }
    }
}