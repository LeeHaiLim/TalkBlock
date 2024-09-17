package com.dev.block.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dev.block.ui.theme.BlockDp
import com.dev.block.ui.theme.BlockTheme

@Composable
fun SwitchWithTitle(
    title: String,
    onCheckedChange: (Boolean) -> Unit,
    checked: Boolean
) {
    Row {
        Text(
            text = title,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = BlockDp.paddingSmall),
            style = MaterialTheme.typography.titleLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSwitchWithLabel() {
    BlockTheme {
        SwitchWithTitle(
            title = "title",
            onCheckedChange = {},
            checked = false
        )
    }
}