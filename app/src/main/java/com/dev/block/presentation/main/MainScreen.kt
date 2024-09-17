package com.dev.block.presentation.main

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dev.block.BuildConfig
import com.dev.block.R
import com.dev.block.presentation.email.RegisterEmailScreen
import com.dev.block.presentation.home.HomeScreen
import com.dev.block.presentation.model.ActionButtonType
import com.dev.block.presentation.setting.SettingScreen
import com.dev.block.ui.component.BlockTopAppBar
import com.dev.block.ui.component.LoadingDialog
import com.dev.block.ui.theme.BlockTheme
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun MainScreen(
    contentObserver: ContentObserver,
    requestPermissionLauncher: ActivityResultLauncher<String>,
    devicePolicyManager: DevicePolicyManager,
    deviceAdminLauncher: ActivityResultLauncher<Intent>,
    deviceAdminComponent: ComponentName,
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BlockTheme {
        Column {
            BlockTopAppBar(
                title = uiState.title,
                onBackClick = { navController.popBackStack() },
                showBackButton = uiState.showBackButton
            ) {
                uiState.actionButtonTypes.forEach {
                    when (it) {
                        ActionButtonType.SETTING ->
                            IconButton(onClick = { navController.navigate("setting") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Settings,
                                    contentDescription = "setting icon"
                                )
                            }
                    }
                }
            }
            NavHost(
                modifier = Modifier.weight(1f),
                navController = navController,
                startDestination = "home",
                exitTransition = { ExitTransition.None },
            ) {
                composable("home") {
                    HomeScreen(
                        navController = navController,
                        showLoading = { show -> viewModel.setLoading(show) }
                    )
                    viewModel.setBackButton(false)
                    viewModel.setActionButtons(listOf(ActionButtonType.SETTING))
                    viewModel.setTitle(stringResource(id = R.string.app_name))
                }
                composable("registerEmail") {
                    RegisterEmailScreen(
                        navController = navController,
                        showLoading = { show -> viewModel.setLoading(show) }
                    )
                    viewModel.setBackButton(true)
                    viewModel.setActionButtons(emptyList())
                    viewModel.setTitle(stringResource(id = R.string.register_email_title))
                }
                composable("setting") {
                    SettingScreen()
                    viewModel.setBackButton(true)
                    viewModel.setActionButtons(emptyList())
                    viewModel.setTitle(stringResource(R.string.setting))
                }
            }
            BannersAds()
        }

        if (!uiState.isAccApproved) {
            AccessibilityPermissionDialog(
                title = stringResource(id = R.string.title_request_accessibility_permission),
                text = stringResource(id = R.string.content_request_accessibility_permission),
                context = context,
                contentObserver = contentObserver
            )
        } else if (!uiState.isAccRunning) {
            context.contentResolver.unregisterContentObserver(contentObserver)
            AccessibilityPermissionDialog(
                title = stringResource(id = R.string.title_fix_accessibility_permission),
                text = stringResource(id = R.string.content_fix_accessibility_permission),
                context = context,
                contentObserver = contentObserver
            )
        } else if (!uiState.isNotificationStored) {
            context.contentResolver.unregisterContentObserver(contentObserver)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else if (!uiState.isDeviceAdminStored) {
            context.contentResolver.unregisterContentObserver(contentObserver)
            if (!devicePolicyManager.isAdminActive(deviceAdminComponent)) {
                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                    putExtra(
                        DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminComponent
                    )
                    putExtra(
                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        stringResource(R.string.device_admin_permission_explanation)
                    )
                }
                deviceAdminLauncher.launch(intent)
            }
        } else {
            context.contentResolver.unregisterContentObserver(contentObserver)
        }

        if (uiState.showLoading) {
            LoadingDialog()
        }
    }
}

@Composable
fun AccessibilityPermissionDialog(
    title: String,
    text: String,
    context: Context,
    contentObserver: ContentObserver
) {
    AlertDialog(
        onDismissRequest = { context.contentResolver.unregisterContentObserver(contentObserver) },
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(
                onClick = {
                    context.contentResolver.registerContentObserver(
                        Settings.Secure.getUriFor(Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES),
                        false,
                        contentObserver
                    )
                    context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                }) { Text(stringResource(id = R.string.approve)) }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    context.contentResolver.unregisterContentObserver(contentObserver)
                    (context as? ComponentActivity)?.finish()
                    Toast.makeText(
                        context,
                        context.getString(R.string.accessibility_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                }) { Text(stringResource(id = R.string.deny)) }
        }
    )
}

@Composable
fun BannersAds() {
    val adId = BuildConfig.ADMOB_UNIT_ID
    AndroidView(
        modifier = Modifier.fillMaxWidth(),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = adId
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { adView -> adView.loadAd(AdRequest.Builder().build()) }
    )
}