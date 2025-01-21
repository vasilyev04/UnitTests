package com.vasilyev.unittests.timer.domain.repository

import kotlinx.coroutines.flow.Flow

interface TimerRepository {
    suspend fun saveTimerValue(value: Long)

    fun getTimerValue(): Flow<Long>
}