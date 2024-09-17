package com.dev.block.presentation.email

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dev.block.R
import com.dev.block.presentation.model.RegisterEmailUiEvent
import com.dev.block.presentation.model.RegisterEmailUiState
import com.dev.block.presentation.model.TextInputState
import com.dev.block.ui.component.OtpTextField
import com.dev.block.ui.component.VerifyEmailTextField
import com.dev.block.ui.theme.BlockDp
import com.dev.block.ui.theme.BlockTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun RegisterEmailScreen(
    navController: NavHostController,
    showLoading: (Boolean) -> Unit,
    viewModel: RegisterEmailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                RegisterEmailUiEvent.OtpMatchEvent -> viewModel.setEmail()
                RegisterEmailUiEvent.OtpNotMatchEvent -> viewModel.onOtpWrong()
                RegisterEmailUiEvent.NavigationEvent -> navController.popBackStack()
                RegisterEmailUiEvent.ShowLoadingEvent -> showLoading(true)
                RegisterEmailUiEvent.HideLoadingEvent -> showLoading(false)
                is RegisterEmailUiEvent.ToastEvent -> {
                    Toast.makeText(
                        context,
                        event.resId.fold("") { acc, id -> acc + context.getString(id) },
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            RegisterEmailScreenContent(
                uiState = uiState,
                onEmailValueChange = { value ->
                    viewModel.onTypedEmailValueChange(value)
                },
                onOtpValueChange = { value ->
                    viewModel.onTypedOtpValueChange(value)
                },
                onVerifyEmailClick = {
                    viewModel.sendVerifyEmail(
                        title = context.getString(R.string.verify_email_title),
                        contentDescription = context.getString(R.string.verify_email_description),
                        extraDescription = context.getString(R.string.email_extra_description)
                    )
                    focusManager.clearFocus()
                    keyboardController?.hide()
                },
                onConfirmButtonClick = {
                    viewModel.updateOtpState()
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            )
        }
    }
}

@Composable
fun RegisterEmailScreenContent(
    uiState: RegisterEmailUiState,
    onEmailValueChange: (String) -> Unit,
    onOtpValueChange: (String) -> Unit,
    onVerifyEmailClick: () -> Unit,
    onConfirmButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(BlockDp.paddingLarge)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            space = BlockDp.paddingSmall,
            alignment = Alignment.CenterVertically
        )
    ) {
        VerifyEmailTextField(
            value = uiState.typedEmailValue,
            onValueChange = onEmailValueChange,
            onVerifyClick = onVerifyEmailClick,
            isError = uiState.emailState == TextInputState.INVALID,
            enabled = uiState.emailState == TextInputState.NONE,
            onSubmit = { if (uiState.emailState == TextInputState.NONE) onVerifyEmailClick() },
            supportingText = {
                when (uiState.emailState) {
                    TextInputState.INVALID -> Text(
                        text = stringResource(id = R.string.email_invalid)
                    )

                    TextInputState.VALID -> Text(
                        text = stringResource(id = R.string.otp_sent),
                        color = MaterialTheme.colorScheme.primary
                    )

                    else -> Text(text = "")
                }
            }
        )
        OtpTextField(
            value = uiState.typedOtpValue,
            onValueChange = onOtpValueChange,
            isError = uiState.otpState == TextInputState.INVALID,
            enabled = uiState.emailState == TextInputState.VALID,
            onSubmit = { if (uiState.otpState == TextInputState.NONE) onConfirmButtonClick() },
            supportingText = {
                val remainTime = stringResource(
                    id = R.string.time_format,
                    uiState.remainingTime / integerResource(id = R.integer.sec_per_min),
                    uiState.remainingTime % integerResource(id = R.integer.sec_per_min)
                )
                when {
                    uiState.otpState == TextInputState.INVALID -> Text(
                        text = remainTime + stringResource(id = R.string.otp_wrong)
                    )

                    uiState.emailState == TextInputState.VALID -> Text(
                        text = remainTime,
                        color = MaterialTheme.colorScheme.primary
                    )

                    else -> Text(text = "")
                }
            }
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = BlockDp.paddingDefault),
            enabled = uiState.otpState == TextInputState.NONE,
            onClick = onConfirmButtonClick,
            content = { Text(text = stringResource(id = R.string.confirm)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterEmailScreen() {
    BlockTheme {
        RegisterEmailScreenContent(
            uiState = RegisterEmailUiState(
                emailState = TextInputState.VALID
            ),
            onEmailValueChange = {},
            onOtpValueChange = {},
            onVerifyEmailClick = {},
            onConfirmButtonClick = {}
        )
    }
}