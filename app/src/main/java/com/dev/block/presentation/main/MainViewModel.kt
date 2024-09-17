package com.dev.block.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.block.data.repository.PermissionRepository
import com.dev.block.presentation.model.ActionButtonType
import com.dev.block.presentation.model.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val permissionRepository: PermissionRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        combine(
            permissionRepository.getAccessibilityState(),
            permissionRepository.getAccessibilityRunningState(),
            permissionRepository.containsNotificationState(),
            permissionRepository.containsAdminState()
        ) { isAccOn, isAccRun, containsNoti, containsAdmin ->
            _uiState.update {
                it.copy(
                    isAccApproved = isAccOn,
                    isAccRunning = isAccRun,
                    isNotificationStored = containsNoti,
                    isDeviceAdminStored = containsAdmin
                )
            }
        }.launchIn(viewModelScope)
    }

    fun setAccessibilityState(isOn: Boolean) {
        permissionRepository.storeAccessibilityState(isOn)
            .catch { }
            .launchIn(viewModelScope)
    }

    fun setNotificationState(isOn: Boolean) {
        permissionRepository.storeNotificationState(isOn)
            .catch { }
            .launchIn(viewModelScope)
    }

    fun setDeviceAdminState(isAdmin: Boolean) {
        permissionRepository.storeAdminState(isAdmin)
            .catch { }
            .launchIn(viewModelScope)
    }

    fun setBackButton(show: Boolean) {
        _uiState.update {
            it.copy(showBackButton = show)
        }
    }

    fun setActionButtons(actionButtons: List<ActionButtonType>) {
        _uiState.update {
            it.copy(actionButtonTypes = actionButtons)
        }
    }

    fun setTitle(title: String) {
        _uiState.update {
            it.copy(title = title)
        }
    }

    fun setLoading(show: Boolean) {
        _uiState.update {
            it.copy(showLoading = show)
        }
    }
}