package com.dev.block.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreHelper @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    fun getEmail(): Flow<String?> = dataStore.data.map { it[EMAIL] }
    fun getPassword(): Flow<String?> = dataStore.data.map { it[PASSWORD] }
    fun getBlockState(): Flow<Boolean?> = dataStore.data.map { it[IS_BLOCK_ON] }
    fun getNotificationState(): Flow<Boolean?> = dataStore.data.map { it[IS_NOTIFICATION_ON] }
    fun getAdminState(): Flow<Boolean?> = dataStore.data.map { it[IS_ADMIN] }
    fun getAccessibilityState(): Flow<Boolean?> = dataStore.data.map { it[IS_ACCESSIBILITY_ON] }
    fun getAccessibilityRunningState(): Flow<Boolean?> =
        dataStore.data.map { it[IS_ACCESSIBILITY_RUNNING] }

    suspend fun storeEmail(email: String) {
        dataStore.edit { it[EMAIL] = email }
    }

    suspend fun storePassword(password: String) {
        dataStore.edit { it[PASSWORD] = password }
    }

    suspend fun storeBlockState(isOn: Boolean) {
        dataStore.edit { it[IS_BLOCK_ON] = isOn }
    }

    suspend fun storeNotificationState(isOn: Boolean) {
        dataStore.edit { it[IS_NOTIFICATION_ON] = isOn }
    }

    suspend fun storeAdminState(isAdmin: Boolean) {
        dataStore.edit { it[IS_ADMIN] = isAdmin }
    }

    suspend fun storeAccessibilityState(isOn: Boolean) {
        dataStore.edit { it[IS_ACCESSIBILITY_ON] = isOn }
    }

    suspend fun storeAccessibilityRunningState(isRunning: Boolean) {
        dataStore.edit { it[IS_ACCESSIBILITY_RUNNING] = isRunning }
    }

    companion object {
        private val IS_BLOCK_ON = booleanPreferencesKey("isOn")
        private val EMAIL = stringPreferencesKey("email")
        private val PASSWORD = stringPreferencesKey("password")
        private val IS_NOTIFICATION_ON = booleanPreferencesKey("isNotificationOn")
        private val IS_ADMIN = booleanPreferencesKey("isAdmin")
        private val IS_ACCESSIBILITY_ON = booleanPreferencesKey("isAccessibilityOn")
        private val IS_ACCESSIBILITY_RUNNING = booleanPreferencesKey("isAccessibilityRunning")
    }
}