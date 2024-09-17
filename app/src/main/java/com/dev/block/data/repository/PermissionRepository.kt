package com.dev.block.data.repository

import com.dev.block.data.datasource.DataStoreHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PermissionRepository @Inject constructor(
    private val dataStoreHelper: DataStoreHelper
) {
    fun storeNotificationState(isOn: Boolean): Flow<Unit> {
        return flow {
            emit(dataStoreHelper.storeNotificationState(isOn))
        }
    }

    fun storeAdminState(isAdmin: Boolean): Flow<Unit> {
        return flow {
            emit(dataStoreHelper.storeAdminState(isAdmin))
        }
    }

    fun storeAccessibilityState(isOn: Boolean): Flow<Unit> {
        return flow {
            emit(dataStoreHelper.storeAccessibilityState(isOn))
        }
    }

    fun storeAccessibilityRunningState(isRunning: Boolean): Flow<Unit> {
        return flow {
            emit(dataStoreHelper.storeAccessibilityRunningState(isRunning))
        }
    }

    fun containsNotificationState(): Flow<Boolean> {
        return dataStoreHelper.getNotificationState().map { it != null }
    }

    fun containsAdminState(): Flow<Boolean> {
        return dataStoreHelper.getAdminState().map { it != null }
    }

    fun getAccessibilityState(): Flow<Boolean> {
        return dataStoreHelper.getAccessibilityState().map { it ?: false }
    }

    fun getAccessibilityRunningState(): Flow<Boolean> {
        return dataStoreHelper.getAccessibilityRunningState().map { it ?: false }
    }
}