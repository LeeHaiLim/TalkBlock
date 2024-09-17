package com.dev.block.presentation.main

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.dev.block.presentation.BlockAccessibilityService
import com.dev.block.presentation.MyDeviceAdminReceiver
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val devicePolicyManager: DevicePolicyManager by lazy {
        getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }
    private val deviceAdminComponent: ComponentName by lazy {
        ComponentName(this, MyDeviceAdminReceiver::class.java)
    }

    private val contentObserver: ContentObserver =
        object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                if (hasAccessibilityPermissions()) {
                    val intent = Intent(this@MainActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    this@MainActivity.startActivity(intent)
                }
            }
        }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean -> viewModel.setNotificationState(isGranted) }

    private val deviceAdminLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> viewModel.setDeviceAdminState(result.resultCode == RESULT_OK) }

    private fun hasAccessibilityPermissions(): Boolean {
        val enabledServices =
            Settings.Secure
                .getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
                ?.split(":")

        return enabledServices?.any { it.startsWith(packageName) } ?: false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startService(Intent(this, BlockAccessibilityService::class.java))
        setContent {
            MainScreen(
                contentObserver = contentObserver,
                requestPermissionLauncher = requestPermissionLauncher,
                viewModel = viewModel,
                deviceAdminComponent = deviceAdminComponent,
                deviceAdminLauncher = deviceAdminLauncher,
                devicePolicyManager = devicePolicyManager
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setAccessibilityState(hasAccessibilityPermissions())
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(contentObserver)
    }
}
