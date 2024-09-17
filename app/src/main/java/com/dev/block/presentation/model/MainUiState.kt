package com.dev.block.presentation.model

data class MainUiState(
    val isAccApproved: Boolean = true,
    val isAccRunning: Boolean = true,
    val isDeviceAdminStored: Boolean = true,
    val isNotificationStored: Boolean = true,
    val title: String = "",
    val showBackButton: Boolean = false,
    val showLoading: Boolean = false,
    val actionButtonTypes: List<ActionButtonType> = listOf(),
)

enum class ActionButtonType { SETTING }