package com.dev.block.presentation.home.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.block.data.BlockException
import com.dev.block.data.repository.EmailRepository
import com.dev.block.data.repository.PasswordRepository
import com.dev.block.presentation.model.PwDialogUiState
import com.dev.block.presentation.model.SetPwUiEvent
import com.dev.block.presentation.model.TextInputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SetPasswordViewModel @Inject constructor(
    emailRepository: EmailRepository,
    private val passwordRepository: PasswordRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PwDialogUiState())
    val uiState: StateFlow<PwDialogUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<SetPwUiEvent>()
    val uiEvent: SharedFlow<SetPwUiEvent> = _uiEvent.asSharedFlow()

    init {
        emailRepository.containsEmail()
            .onEach { containsEmail ->
                _uiState.update {
                    it.copy(isEmailRegistered = containsEmail)
                }
            }.launchIn(viewModelScope)
    }

    fun initUiState() {
        _uiState.update { PwDialogUiState(isEmailRegistered = it.isEmailRegistered) }
    }

    fun onTypedValueChange(value: String) {
        if (value.length > PasswordRepository.PW_SIZE_MAX) return
        _uiState.update {
            it.copy(
                typedValue = value,
                pwState = pwStateOf(value)
            )
        }
    }

    fun changePwVisibility() {
        _uiState.update {
            it.copy(isVisible = !it.isVisible)
        }
    }

    fun setPassword() {
        passwordRepository.storeHashedPassword(uiState.value.typedValue)
            .onEach {
                _uiEvent.emit(SetPwUiEvent.NavigationEvent)
            }.catch { e ->
                if (e is BlockException) _uiEvent.emit(SetPwUiEvent.ToastEvent(*e.args))
            }.onStart {
                _uiEvent.emit(SetPwUiEvent.ShowLoadingEvent)
            }.onCompletion {
                _uiEvent.emit(SetPwUiEvent.HideLoadingEvent)
            }.launchIn(viewModelScope)
    }

    private fun pwStateOf(value: String): TextInputState {
        return if (passwordRepository.isPasswordValid(value)) TextInputState.VALID
        else if (value.isEmpty()) TextInputState.EMPTY
        else TextInputState.INVALID
    }
}