package com.dev.block.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.dev.block.R
import com.dev.block.ui.theme.BlockDp

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isTextVisible: Boolean,
    onClickVisibility: () -> Unit,
    maxLength: Int = integerResource(id = R.integer.text_input_limit),
    isError: Boolean,
    onSubmit: () -> Unit = {},
    supportingText: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = BlockDp.paddingDefault, bottom = BlockDp.paddingSmall),
        value = value,
        onValueChange = { if (it.length <= maxLength) onValueChange(it) },
        placeholder = { Text(text = stringResource(id = R.string.pw)) },
        trailingIcon = {
            val painter =
                if (isTextVisible) painterResource(id = R.drawable.baseline_visibility_off_24)
                else painterResource(id = R.drawable.baseline_visibility_24)

            IconButton(onClick = onClickVisibility) {
                Icon(
                    painter = painter,
                    contentDescription = if (isTextVisible) "Hide password" else "Show password"
                )
            }
        },
        isError = isError,
        visualTransformation = if (isTextVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
        supportingText = supportingText,
        singleLine = true
    )
}