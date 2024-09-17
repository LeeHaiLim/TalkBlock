package com.dev.block.presentation.model

data class RegisterEmailUiState(
    val typedEmailValue: String = "",
    val typedOtpValue: String = "",
    val otpNumber: String? = null,
    val emailState: TextInputState = TextInputState.EMPTY,
    val otpState: TextInputState = TextInputState.EMPTY,
    val remainingTime: Int = 0,
    val showLoading: Boolean = false
)