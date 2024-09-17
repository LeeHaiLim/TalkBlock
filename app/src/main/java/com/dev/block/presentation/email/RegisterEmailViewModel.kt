package com.dev.block.presentation.email

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.block.data.BlockException
import com.dev.block.data.repository.EmailRepository
import com.dev.block.presentation.model.RegisterEmailUiEvent
import com.dev.block.presentation.model.RegisterEmailUiState
import com.dev.block.presentation.model.TextInputState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class RegisterEmailViewModel @Inject constructor(
    private val emailRepository: EmailRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterEmailUiState())
    val uiState: StateFlow<RegisterEmailUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<RegisterEmailUiEvent>()
    val uiEvent: SharedFlow<RegisterEmailUiEvent> = _uiEvent.asSharedFlow()


    fun onTypedEmailValueChange(value: String) {
        if (value.length > EmailRepository.EMAIL_SIZE_MAX) return
        _uiState.update {
            RegisterEmailUiState(
                typedEmailValue = value,
                emailState = emailStateOf(value),
            )
        }
    }

    fun sendVerifyEmail(
        title: String,
        contentDescription: String,
        extraDescription: String
    ) {
        val otpNumber = generateOtpNumber()
        emailRepository.sendEmail(
            uiState.value.typedEmailValue,
            title = title,
            content = otpNumber,
            contentDescription = contentDescription,
            extraDescription = extraDescription
        ).onEach {
            onVerifyStart(otpNumber)
            countDown()
        }.catch { e ->
            if (e is BlockException) _uiEvent.emit(RegisterEmailUiEvent.ToastEvent(*e.args))
        }.onStart {
            _uiEvent.emit(RegisterEmailUiEvent.ShowLoadingEvent)
        }.onCompletion {
            _uiEvent.emit(RegisterEmailUiEvent.HideLoadingEvent)
        }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }

    private fun onVerifyStart(otpNumber: String) {
        _uiState.update {
            it.copy(
                otpNumber = otpNumber,
                remainingTime = VERIFY_TIME_LIMIT,
                emailState = TextInputState.VALID
            )
        }
    }

    private fun countDown() {
        viewModelScope.launch {
            while (_uiState.value.remainingTime > 0) {
                delay(1000L)
                _uiState.update { it.copy(remainingTime = it.remainingTime - 1) }
            }
            onVerifyEnd()
        }
    }

    private fun onVerifyEnd() {
        _uiState.update {
            RegisterEmailUiState(
                typedEmailValue = it.typedEmailValue,
                emailState = emailStateOf(it.typedEmailValue),
            )
        }
    }

    fun onTypedOtpValueChange(value: String) {
        if (value.length > OTP_SIZE) return
        _uiState.update {
            it.copy(
                typedOtpValue = value,
                otpState = if (value.length == OTP_SIZE) TextInputState.NONE
                else TextInputState.EMPTY
            )
        }
    }

    fun updateOtpState() {
        viewModelScope.launch {
            with(uiState.value) {
                if (typedOtpValue == otpNumber) _uiEvent.emit(RegisterEmailUiEvent.OtpMatchEvent)
                else _uiEvent.emit(RegisterEmailUiEvent.OtpNotMatchEvent)
            }
        }
    }

    fun setEmail() {
        _uiState.update { it.copy(showLoading = true) }
        emailRepository.storeEmail(uiState.value.typedEmailValue)
            .onEach {
                _uiEvent.emit(RegisterEmailUiEvent.NavigationEvent)
            }.catch { e ->
                if (e is BlockException) _uiEvent.emit(RegisterEmailUiEvent.ToastEvent(*e.args))
            }.onCompletion {
                _uiState.update { it.copy(showLoading = false) }
            }.launchIn(viewModelScope)
    }

    fun onOtpWrong() {
        _uiState.update {
            it.copy(otpState = TextInputState.INVALID)
        }
    }

    private fun emailStateOf(typedValue: String): TextInputState {
        return if (typedValue.isEmpty()) TextInputState.EMPTY
        else if (emailRepository.isValidEmail(typedValue)) TextInputState.NONE
        else TextInputState.INVALID
    }

    private fun generateOtpNumber(): String {
        val maxNumber = 10.0.pow(OTP_SIZE.toDouble()).toInt()
        return String.format("%0${OTP_SIZE}d", (0..<maxNumber).random())
    }

    companion object {
        private const val OTP_SIZE = 6
        private const val VERIFY_TIME_LIMIT = 180
    }
}