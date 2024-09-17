package com.dev.block.presentation.model

import androidx.annotation.StringRes

sealed class RegisterEmailUiEvent {
    data object OtpMatchEvent : RegisterEmailUiEvent()
    data object OtpNotMatchEvent : RegisterEmailUiEvent()
    data object NavigationEvent : RegisterEmailUiEvent()
    data object ShowLoadingEvent : RegisterEmailUiEvent()
    data object HideLoadingEvent : RegisterEmailUiEvent()
    class ToastEvent(@StringRes vararg val resId: Int) : RegisterEmailUiEvent()
}

sealed class SetPwUiEvent {
    data object NavigationEvent : SetPwUiEvent()
    data object ShowLoadingEvent : SetPwUiEvent()
    data object HideLoadingEvent : SetPwUiEvent()
    class ToastEvent(@StringRes vararg val resId: Int) : SetPwUiEvent()
}

sealed class ConfirmPwUiEvent {
    data object PwMatchEvent : ConfirmPwUiEvent()
    data object PwNotMatchEvent : ConfirmPwUiEvent()
    data object ShowLoadingEvent : ConfirmPwUiEvent()
    data object HideLoadingEvent : ConfirmPwUiEvent()
    class ToastEvent(@StringRes vararg val resId: Int) : ConfirmPwUiEvent()
}