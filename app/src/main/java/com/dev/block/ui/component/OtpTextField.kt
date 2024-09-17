package com.dev.block.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.dev.block.R

@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
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
        placeholder = { Text(text = stringResource(id = R.string.otp)) },
        supportingText = supportingText,
        isError = isError,
        enabled = enabled,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        singleLine = true
    )
}