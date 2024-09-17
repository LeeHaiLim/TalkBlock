package com.dev.block.presentation.home.dialog

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.block.R
import com.dev.block.presentation.model.ConfirmPwUiEvent
import com.dev.block.presentation.model.PwDialogUiState
import com.dev.block.presentation.model.TextInputState
import com.dev.block.ui.component.PasswordTextField
import com.dev.block.ui.theme.BlockDp
import com.dev.block.ui.theme.BlockTheme
import com.dev.block.ui.theme.BlockTypo
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ConfirmPasswordDialog(
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    showLoading: (Boolean) -> Unit,
    viewModel: ConfirmPasswordViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                ConfirmPwUiEvent.PwMatchEvent -> {
                    viewModel.initUiState()
                    onConfirmButtonClick()
                }

                ConfirmPwUiEvent.PwNotMatchEvent -> viewModel.onPasswordWrong()
                ConfirmPwUiEvent.ShowLoadingEvent -> showLoading(true)
                ConfirmPwUiEvent.HideLoadingEvent -> showLoading(false)
                is ConfirmPwUiEvent.ToastEvent -> {
                    Toast.makeText(
                        context,
                        event.resId.fold("") { acc, id -> acc + context.getString(id) },
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    ConfirmPasswordDialogContent(
        uiState = uiState,
        onDismissRequest = {
            viewModel.initUiState()
            onDismissRequest()
        },
        onPWFieldValueChange = { value -> viewModel.onTypedValueChange(value) },
        onClickVisibility = { viewModel.changePwVisibility() },
        onConfirmButtonClick = { viewModel.updatePwState() },
        onPasswordRegainClick = {
            viewModel.sendRegainPwEmail(
                title = context.getString(R.string.regain_pw_email_title),
                contentDescription = context.getString(R.string.regain_pw_email_description),
                extraDescription = context.getString(R.string.email_extra_description)
            )
        }
    )
}

@Composable
private fun ConfirmPasswordDialogContent(
    uiState: PwDialogUiState,
    onDismissRequest: () -> Unit,
    onPWFieldValueChange: (String) -> Unit,
    onClickVisibility: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    onPasswordRegainClick: () -> Unit
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
                    text = stringResource(id = R.string.pw),
                    style = MaterialTheme.typography.titleLarge
                )
                PasswordTextField(
                    value = uiState.typedValue,
                    onValueChange = onPWFieldValueChange,
                    isTextVisible = uiState.isVisible,
                    onClickVisibility = onClickVisibility,
                    maxLength = integerResource(id = R.integer.text_input_limit),
                    isError = uiState.pwState == TextInputState.INVALID,
                    onSubmit = {
                        if (uiState.pwState == TextInputState.NONE) onConfirmButtonClick()
                    },
                    supportingText = {
                        Text(
                            text = if (uiState.pwState == TextInputState.INVALID) {
                                stringResource(id = R.string.pw_wrong)
                            } else ""
                        )
                    }
                )
                Button(
                    onClick = onConfirmButtonClick,
                    enabled = uiState.pwState == TextInputState.NONE,
                    content = { Text(text = stringResource(id = R.string.confirm)) }
                )
                Spacer(modifier = Modifier.padding(BlockDp.paddingSmall))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.confirm_pw_warning),
                    style = BlockTypo.warning(),
                )
                Text(
                    modifier = Modifier
                        .clickable { onPasswordRegainClick() }
                        .align(Alignment.Start)
                        .padding(vertical = BlockDp.paddingXSmall),
                    text = stringResource(id = R.string.pw_regain),
                    style = BlockTypo.textButton,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewConfirmPasswordDialog() {
    BlockTheme {
        ConfirmPasswordDialogContent(
            uiState = PwDialogUiState(),
            onDismissRequest = {},
            onPWFieldValueChange = {},
            onClickVisibility = {},
            onConfirmButtonClick = {},
            onPasswordRegainClick = {}
        )
    }
}