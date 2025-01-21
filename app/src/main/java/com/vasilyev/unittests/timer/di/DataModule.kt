package com.vasilyev.unittests.timer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.vasilyev.unittests.timer.data.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    companion object {
        private const val DATA_STORE_FILE_NAME = "TIMER_DATA_STORE"
        private val Context.dataStore by preferencesDataStore(DATA_STORE_FILE_NAME)

        @Provides
        @Singleton
        fun provideDataStore(
            @ApplicationContext context: Context
        ): DataStore<Preferences> {
            return context.dataStore
        }

        @Provides
        @Singleton
        fun provideDataStoreManager(
            dataStorePreferences: DataStore<Preferences>
        ): DataStoreManager {
            return DataStoreManager(dataStorePreferences)
        }
    }
}