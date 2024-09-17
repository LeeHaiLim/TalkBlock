package com.dev.block.presentation.home.dialog

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.block.R
import com.dev.block.presentation.model.PwDialogUiState
import com.dev.block.presentation.model.SetPwUiEvent
import com.dev.block.presentation.model.TextInputState
import com.dev.block.ui.component.PasswordTextField
import com.dev.block.ui.theme.BlockDp
import com.dev.block.ui.theme.BlockTheme
import com.dev.block.ui.theme.BlockTypo
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SetPasswordDialog(
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    onRegisterEmailClick: () -> Unit,
    showLoading: (Boolean) -> Unit,
    viewModel: SetPasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                SetPwUiEvent.NavigationEvent -> {
                    viewModel.initUiState()
                    onConfirmButtonClick()
                }

                SetPwUiEvent.ShowLoadingEvent -> showLoading(true)
                SetPwUiEvent.HideLoadingEvent -> showLoading(false)
                is SetPwUiEvent.ToastEvent -> {
                    Toast.makeText(
                        context,
                        event.resId.fold("") { acc, id -> acc + context.getString(id) },
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    SetPasswordDialogContent(
        uiState = uiState,
        onDismissRequest = {
            viewModel.initUiState()
            onDismissRequest()
        },
        onPWFieldValueChange = { value -> viewModel.onTypedValueChange(value) },
        onClickVisibility = { viewModel.changePwVisibility() },
        onConfirmButtonClick = { viewModel.setPassword() },
        onRegisterEmailClick = onRegisterEmailClick
    )
}

@Composable
private fun SetPasswordDialogContent(
    uiState: PwDialogUiState,
    onDismissRequest: () -> Unit,
    onPWFieldValueChange: (String) -> Unit,
    onClickVisibility: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    onRegisterEmailClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(BlockDp.paddingDefault),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(BlockDp.paddingDefault),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(id = R.string.set_pw),
                    style = MaterialTheme.typography.titleLarge
                )
                PasswordTextField(
                    value = uiState.typedValue,
                    onValueChange = onPWFieldValueChange,
                    isTextVisible = uiState.isVisible,
                    onClickVisibility = onClickVisibility,
                    isError = uiState.pwState == TextInputState.INVALID,
                    supportingText = {
                        if (uiState.pwState == TextInputState.VALID) {
                            Text(
                                text = stringResource(id = R.string.pw_valid),
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(text = stringResource(id = R.string.pw_invalid))
                        }

                    },
                    onSubmit = {
                        if (uiState.pwState == TextInputState.VALID) onConfirmButtonClick()
                    }
                )
                Button(
                    onClick = onConfirmButtonClick,
                    enabled = uiState.pwState == TextInputState.VALID,
                    content = { Text(text = stringResource(id = R.string.confirm)) }
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.set_pw_warning) +
                            stringResource(
                                id = if (uiState.isEmailRegistered) R.string.registered
                                else R.string.not_registered
                            ),
                    style = BlockTypo.warning(),
                )
                Text(
                    modifier = Modifier
                        .clickable { onRegisterEmailClick() }
                        .align(Alignment.Start)
                        .padding(vertical = BlockDp.paddingXSmall),
                    text = stringResource(
                        id = if (uiState.isEmailRegistered) R.string.change_email
                        else R.string.register_email
                    ),
                    style = BlockTypo.textButton,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSetPasswordDialog() {
    BlockTheme {
        SetPasswordDialogContent(
            uiState = PwDialogUiState(pwState = TextInputState.VALID),
            onDismissRequest = {},
            onPWFieldValueChange = {},
            onClickVisibility = {},
            onConfirmButtonClick = {},
            onRegisterEmailClick = {}
        )
    }
}