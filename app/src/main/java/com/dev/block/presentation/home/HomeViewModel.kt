package com.dev.block.presentation.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.block.data.BlockException
import com.dev.block.data.repository.BlockStateRepository
import com.dev.block.presentation.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

sealed class HomeUiEvent {
    class ToastEvent(@StringRes vararg val resId: Int) : HomeUiEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val blockStateRepository: BlockStateRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

    init {
        blockStateRepository.getBlockState()
            .onEach { isOn ->
                _uiState.update {
                    it.copy(isSwitchOn = isOn)
                }
            }.launchIn(viewModelScope)
    }

    fun turnOn() {
        blockStateRepository.storeBlockState(true)
            .catch { e ->
                if (e is BlockException) _uiEvent.emit(HomeUiEvent.ToastEvent(*e.args))
            }.launchIn(viewModelScope)
    }

    fun turnOff() {
        blockStateRepository.storeBlockState(false)
            .catch { e ->
                if (e is BlockException) _uiEvent.emit(HomeUiEvent.ToastEvent(*e.args))
            }.launchIn(viewModelScope)
    }

    fun showSetPWDialog() {
        _uiState.update {
            it.copy(showSetPassword = true)
        }
    }

    fun showConfirmPWDialog() {
        _uiState.update {
            it.copy(showConfirmPassword = true)
        }
    }

    fun dismissSetPWDialog() {
        _uiState.update {
            it.copy(showSetPassword = false)
        }
    }

    fun dismissConfirmPWDialog() {
        _uiState.update {
            it.copy(showConfirmPassword = false)
        }
    }
}