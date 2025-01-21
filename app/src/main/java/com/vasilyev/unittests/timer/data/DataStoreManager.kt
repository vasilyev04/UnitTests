package com.vasilyev.unittests.timer.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class DataStoreManager @Inject constructor(
    private val dataStorePreferences: DataStore<Preferences>
) {
    fun getLongFlow(
        key: String, defaultValue: Long
    ): Flow<Long> {
        val preferenceKey = longPreferencesKey(key)

        return dataStorePreferences.data
            .mapNotNull { preferences ->
                preferences[preferenceKey] ?: defaultValue
            }
    }

    suspend fun putLong(key: String, value: Long) {
        val preferenceKey = longPreferencesKey(key)

        dataStorePreferences.edit { preferences ->
            preferences[preferenceKey] = value
        }
    }
}