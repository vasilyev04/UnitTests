package com.vasilyev.unittests.timer.domain.usecase

import com.vasilyev.unittests.timer.domain.repository.TimerRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.math.exp

@OptIn(ExperimentalCoroutinesApi::class)
class GetTimerValueUseCaseTest {
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
    fun `verify that use case returns the exact flow from repository`() = runTest {
        val useCase = GetTimerValueUseCase(timerRepository)

        val expected = flowOf(6000L)

        coEvery { timerRepository.getTimerValue() } returns expected

        val actual = useCase.invoke()

        assertEquals(expected, actual)
    }
}