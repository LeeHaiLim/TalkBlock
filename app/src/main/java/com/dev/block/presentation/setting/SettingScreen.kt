package com.dev.block.presentation.setting

import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.block.R
import com.dev.block.ui.component.SettingItem
import com.dev.block.ui.theme.BlockDp
import com.dev.block.ui.theme.BlockTheme

@Composable
fun SettingScreen() {
    val context = LocalContext.current
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(BlockDp.paddingDefault)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy((-1).dp)
            ) {
                SettingItem(
                    onClick = {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    },
                    title = stringResource(R.string.accessibility_setting_title),
                    description = stringResource(R.string.accessibility_setting_description),
                    painter = painterResource(id = R.drawable.round_accessibility_24)
                )
                SettingItem(
                    onClick = {
                        val intent = Intent().apply {
                            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        context.startActivity(intent)
                    },
                    title = stringResource(R.string.notification_setting_title),
                    description = stringResource(R.string.notification_setting_description),
                    imageVector = Icons.Default.Notifications
                )
                SettingItem(
                    onClick = {
                        val intent = Intent().setComponent(
                            ComponentName(
                                "com.android.settings",
                                "com.android.settings.DeviceAdminSettings"
                            )
                        )
                        context.startActivity(intent)
                    },
                    title = stringResource(R.string.device_admin_setting_title),
                    description = stringResource(R.string.device_admin_setting_description),
                    painter = painterResource(id = R.drawable.baseline_admin_panel_settings_24)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingScreen() {
    BlockTheme {
        SettingScreen()
    }
}