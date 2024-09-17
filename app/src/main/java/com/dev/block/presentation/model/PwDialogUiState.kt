package com.dev.block.presentation.model

data class PwDialogUiState(
    val typedValue: String = "",
    val pwState: TextInputState = TextInputState.EMPTY,
    val isVisible: Boolean = false,
    val isEmailRegistered: Boolean = false
)