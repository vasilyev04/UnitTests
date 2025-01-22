package com.vasilyev.unittests.timer.data.repository

import com.vasilyev.unittests.timer.data.DataStoreManager
import io.mockk.coEvery
import io.mockk.coVerify
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

@OptIn(ExperimentalCoroutinesApi::class)
class TimerRepositoryTest {
    private val testDispatcher = StandardTestDispatcher()
    private val dataStoreManager: DataStoreManager = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `check that repository returns correct flow`() = runTest {
        val expected = 60000L

        coEvery {
            dataStoreManager.getLongFlow(any(), any())
        } returns flowOf(expected)

        val timerRepository = TimerRepositoryImpl(dataStoreManager)

        val actualFlow = timerRepository.getTimerValue()

        actualFlow.collect { actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `verify that timer value actually saves in data store`() = runTest {
        val timerRepository = TimerRepositoryImpl(dataStoreManager)

        timerRepository.saveTimerValue(60000L)

        coVerify(exactly = 1) { dataStoreManager.putLong(any(), any()) }
    }
}