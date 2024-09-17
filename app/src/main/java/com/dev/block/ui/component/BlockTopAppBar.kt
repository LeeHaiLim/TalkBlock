package com.dev.block.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.dev.block.R
import com.dev.block.ui.theme.BlockDp
import com.dev.block.ui.theme.BlockTheme

@Composable
fun BlockTopAppBar(
    title: String,
    onBackClick: () -> Unit = {},
    showBackButton: Boolean = true,
    actionButtons: @Composable RowScope.() -> Unit = {}
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(BlockDp.appBarSize)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showBackButton) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "navigation icon"
                        )
                    }
                } else {
                    Icon(
                        modifier = Modifier.padding(
                            start = BlockDp.paddingDefault,
                            end = BlockDp.paddingSmall
                        ),
                        painter = painterResource(id = R.drawable.ic_stat_name),
                        contentDescription = "app icon"
                    )
                }
                Text(
                    modifier = Modifier.weight(1f),
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                actionButtons()
                Spacer(modifier = Modifier.size(BlockDp.paddingSmall))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBlockTopAppBar1() {
    BlockTheme {
        BlockTopAppBar(
            title = "title"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBlockTopAppBar2() {
    BlockTheme {
        BlockTopAppBar(
            title = "title",
            showBackButton = false
        )
    }
}
