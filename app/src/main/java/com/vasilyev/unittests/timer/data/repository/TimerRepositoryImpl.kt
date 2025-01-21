package com.vasilyev.unittests.timer.data.repository

import com.vasilyev.unittests.timer.data.DataStoreManager
import com.vasilyev.unittests.timer.domain.repository.TimerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimerRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
): TimerRepository {

    companion object {
        private const val TIMER_VALUE_DS_KEY = "TIMER_VALUE"
        private const val DEFAULT_TIMER_VALUE = 0L
    }

    override suspend fun saveTimerValue(value: Long) {
        dataStoreManager.putLong(TIMER_VALUE_DS_KEY, value)
    }

    override fun getTimerValue(): Flow<Long> {
        return dataStoreManager.getLongFlow(
            key = TIMER_VALUE_DS_KEY,
            defaultValue = DEFAULT_TIMER_VALUE
        )
    }
}