package com.vasilyev.unittests.timer.di

import com.vasilyev.unittests.timer.data.repository.TimerRepositoryImpl
import com.vasilyev.unittests.timer.domain.repository.TimerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DomainModule {

    @Binds
    @Singleton
    fun bindTimerRepository(impl: TimerRepositoryImpl): TimerRepository
}