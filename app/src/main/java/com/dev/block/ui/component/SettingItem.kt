package com.dev.block.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import com.dev.block.ui.theme.BlockDp

@Composable
fun SettingItem(
    onClick: () -> Unit,
    title: String,
    description: String,
    imageVector: ImageVector
) {
    HorizontalDivider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = BlockDp.paddingSmall,
                vertical = BlockDp.paddingDefault
            )
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "setting item icon"
        )
        Spacer(modifier = Modifier.size(BlockDp.paddingSmall))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(BlockDp.paddingXSmall))
            Text(
                text = description,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "navigate to setting icon"
        )
    }
    HorizontalDivider()
}

@Composable
fun SettingItem(
    onClick: () -> Unit,
    title: String,
    description: String,
    painter: Painter
) {
    HorizontalDivider()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = BlockDp.paddingSmall,
                vertical = BlockDp.paddingDefault
            )
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = "setting item icon"
        )
        Spacer(modifier = Modifier.size(BlockDp.paddingSmall))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(BlockDp.paddingXSmall))
            Text(
                text = description,
                style = MaterialTheme.typography.labelMedium
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "navigate to setting icon"
        )
    }
    HorizontalDivider()
}