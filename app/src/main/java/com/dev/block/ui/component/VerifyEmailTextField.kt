package com.dev.block.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.dev.block.R
import com.dev.block.ui.theme.BlockDp

@Composable
fun VerifyEmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    maxLength: Int = integerResource(id = R.integer.text_input_limit),
    isError: Boolean,
    enabled: Boolean,
    onSubmit: () -> Unit = {},
    supportingText: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        placeholder = { Text(text = stringResource(id = R.string.email)) },
        supportingText = supportingText,
        trailingIcon = {
            FilledTonalButton(
                modifier = Modifier.padding(end = BlockDp.paddingSmall),
                enabled = enabled,
                onClick = onVerifyClick,
                content = { Text(text = stringResource(id = R.string.verify)) }
            )
        },
        isError = isError,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        singleLine = true
    )
}