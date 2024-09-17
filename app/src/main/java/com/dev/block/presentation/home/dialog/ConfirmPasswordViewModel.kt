package com.dev.block.presentation.home.dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.block.R
import com.dev.block.data.BlockException
import com.dev.block.data.repository.EmailRepository
import com.dev.block.data.repository.PasswordRepository
import com.dev.block.presentation.model.ConfirmPwUiEvent
import com.dev.block.presentation.model.PwDialogUiState
import com.dev.block.presentation.model.TextInputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ConfirmPasswordViewModel @Inject constructor(
    private val pwRepository: PasswordRepository,
    private val emailRepository: EmailRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PwDialogUiState())
    val uiState: StateFlow<PwDialogUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<ConfirmPwUiEvent>()
    val uiEvent: SharedFlow<ConfirmPwUiEvent> = _uiEvent.asSharedFlow()

    fun initUiState() {
        _uiState.update { PwDialogUiState() }
    }

    fun onTypedValueChange(value: String) {
        _uiState.update {
            it.copy(
                typedValue = value,
                pwState = if (value.isEmpty()) TextInputState.EMPTY else TextInputState.NONE
            )
        }
    }

    fun changePwVisibility() {
        _uiState.update {
            it.copy(isVisible = !it.isVisible)
        }
    }

    fun updatePwState() {
        pwRepository.isPasswordMatch(uiState.value.typedValue)
            .onEach {
                if (it) _uiEvent.emit(ConfirmPwUiEvent.PwMatchEvent)
                else _uiEvent.emit(ConfirmPwUiEvent.PwNotMatchEvent)
            }.launchIn(viewModelScope)
    }

    fun onPasswordWrong() {
        _uiState.update {
            it.copy(pwState = TextInputState.INVALID)
        }
    }

    fun sendRegainPwEmail(
        title: String,
        contentDescription: String,
        extraDescription: String
    ) {
        val newPw = pwRepository.generatePassword()
        emailRepository.sendEmail(
            title = title,
            content = newPw,
            contentDescription = contentDescription,
            extraDescription = extraDescription
        ).onEach {
            storeNewPw(newPw)
        }.catch { e ->
            if (e is BlockException) _uiEvent.emit(ConfirmPwUiEvent.ToastEvent(*e.args))
        }.onStart {
            _uiEvent.emit(ConfirmPwUiEvent.ShowLoadingEvent)
        }.onCompletion {
            _uiEvent.emit(ConfirmPwUiEvent.HideLoadingEvent)
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun storeNewPw(pw: String) {
        pwRepository.storeHashedPassword(pw)
            .onEach {
                _uiEvent.emit(ConfirmPwUiEvent.ToastEvent(R.string.email_sent))
            }.catch { e ->
                if (e is BlockException) _uiEvent.emit(ConfirmPwUiEvent.ToastEvent(*e.args))
            }.onStart {
                _uiEvent.emit(ConfirmPwUiEvent.ShowLoadingEvent)
            }.onCompletion {
                _uiEvent.emit(ConfirmPwUiEvent.HideLoadingEvent)
            }.launchIn(viewModelScope)
    }
}