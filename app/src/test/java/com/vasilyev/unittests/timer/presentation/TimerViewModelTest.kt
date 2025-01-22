package com.vasilyev.unittests.timer.presentation

import com.vasilyev.unittests.timer.domain.usecase.GetTimerValueUseCase
import com.vasilyev.unittests.timer.domain.usecase.SaveTimerValueUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val getTimerValueUseCase: GetTimerValueUseCase = mockk(relaxed = true)
    private val saveTimerValueUseCase: SaveTimerValueUseCase = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `check that time gets formatted correctly`() = runTest {
        val listOfTimerValues = listOf(
            60 * 1000L,
            99 * 1000L,
            123 * 1000L,
            150 * 1000L,
            999 * 1000L,
            5490 * 1000L
        )

        val listOfExpectedFormattedTimerValues = listOf(
            "01:00",  // 60 * 1000 = 1 minute
            "01:39",  // 99 * 1000 = 1 minute 39 seconds
            "02:03",  // 123 * 1000 = 2 minutes 3 seconds
            "02:30",  // 150 * 1000 = 2 minutes 30 seconds
            "16:39",  // 999 * 1000 = 16 minutes 39 seconds
            "91:30"   // 5490 * 1000 = 91 minutes 30 seconds
        )

        listOfTimerValues.forEachIndexed { index, value ->
            val viewModel = getDefaultViewModel()

            coEvery { getTimerValueUseCase.invoke() } returns flowOf(value)

            advanceUntilIdle()

            val actual = viewModel.state.value.formattedTimerValue
            val expected = listOfExpectedFormattedTimerValues[index]
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `when StartTimer intent and input is not empty, timer should start`() = runTest {
        val viewModel = getDefaultViewModel()

        coEvery { getTimerValueUseCase.invoke() } returns flowOf(60000)

        with(viewModel) {
            reduce(TimerIntent.ChangeInputTimerValue("60"))
            reduce(TimerIntent.StartTimer)
        }

        advanceUntilIdle()

        val state = viewModel.state.value

        assertTrue(state.isRunning)
        assertEquals(60000, state.timerValue)
        assertEquals("", state.inputTimerValue)
    }


    @Test
    fun `check that timer pause works correctly`() = runTest {
        val viewModel = getDefaultViewModel()

        with(viewModel) {
            reduce(TimerIntent.ChangeInputTimerValue("60"))
            reduce(TimerIntent.StartTimer)
            reduce(TimerIntent.PauseTimer)
        }

        advanceUntilIdle()

        val state = viewModel.state.value

        assertTrue(state.isPaused)
        assertTrue(state.isRunning)
    }

    @Test
    fun `check that timer unpause works correctly`() = runTest {
        val viewModel = getDefaultViewModel()

        with(viewModel) {
            reduce(TimerIntent.ChangeInputTimerValue("60"))
            reduce(TimerIntent.StartTimer)
            reduce(TimerIntent.PauseTimer)
            reduce(TimerIntent.UnPauseTimer)
        }


        advanceUntilIdle()

        val state = viewModel.state.value

        assertFalse(state.isPaused)
        assertTrue(state.isRunning)
    }

    @Test
    fun `pause should not be able if timer is not running`() {
        val viewModel = getDefaultViewModel()

        viewModel.reduce(TimerIntent.PauseTimer)
        val state = viewModel.state.value

        assertFalse(state.isPaused)
        assertFalse(state.isRunning)
    }

    @Test
    fun `check that timer stop works correctly`() = runTest {
        val viewModel = getDefaultViewModel()

        coEvery { getTimerValueUseCase.invoke() } returns flowOf(0)

        with(viewModel) {
            reduce(TimerIntent.ChangeInputTimerValue("60"))
            reduce(TimerIntent.StartTimer)
            reduce(TimerIntent.StopTimer)
        }

        advanceUntilIdle()

        val state = viewModel.state.value

        assertFalse(state.isRunning)
        assertFalse(state.isPaused)

        assertEquals(0, state.timerValue)
        assertEquals("", state.inputTimerValue)
    }


    @Test
    fun `check that time save works correctly`() = runTest {
        val viewModel = getDefaultViewModel()

        coEvery { getTimerValueUseCase.invoke() } returns flowOf(60000)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(60000, state.timerValue)
    }

    @Test
    fun `timer should not be started if negative value passed`() = runTest {
        val viewModel = getDefaultViewModel()

        with(viewModel) {
            reduce(TimerIntent.ChangeInputTimerValue("-60"))
            reduce(TimerIntent.StartTimer)
        }

        advanceUntilIdle()

        val state = viewModel.state.value

        assertFalse(state.isRunning)    
        assertEquals(0, state.timerValue)
    }

    @Test
    fun `empty field error should be shown if empty value passed`() = runTest {
        val viewModel = getDefaultViewModel()

        with(viewModel) {
            reduce(TimerIntent.ChangeInputTimerValue(""))
            reduce(TimerIntent.StartTimer)
        }

        advanceUntilIdle()

        val state = viewModel.state.value

        assertFalse(state.isRunning)
        assertTrue(state.isInputEmpty)
    }

    private fun getDefaultViewModel() = TimerViewModel(getTimerValueUseCase, saveTimerValueUseCase)
}