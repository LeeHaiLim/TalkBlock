package com.dev.block.presentation

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.dev.block.R
import com.dev.block.data.repository.BlockStateRepository
import com.dev.block.data.repository.PermissionRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class BlockAccessibilityService : AccessibilityService() {
    @Inject
    lateinit var blockStateRepository: BlockStateRepository

    @Inject
    lateinit var permissionRepository: PermissionRepository

    private var isBlocked: Boolean = false

    override fun onServiceConnected() {
        super.onServiceConnected()
        changeAccessibilityRunningState(true)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        changeAccessibilityRunningState(false)
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        fetchBlockState()
        changeAccessibilityRunningState(false)
    }

    private fun changeAccessibilityRunningState(isRunning: Boolean) {
        permissionRepository.storeAccessibilityRunningState(isRunning)
            .launchIn(CoroutineScope(Dispatchers.IO))
    }

    private fun fetchBlockState() {
        blockStateRepository.getBlockState()
            .onEach {
                isBlocked = it
                if (isBlocked) startBlockNotificationService()
                else stopBlockNotificationService()
            }.launchIn(CoroutineScope(Dispatchers.IO))
    }


    override fun onDestroy() {
        super.onDestroy()
        stopBlockNotificationService()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event?.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED
        ) {
            if (!isBlocked) return
            if (event.packageName == getString(R.string.target_package_name)) {
                val rootNode = rootInActiveWindow
                if (rootNode != null) {
                    val nodes =
                        rootNode.findAccessibilityNodeInfosByText(getString(R.string.button))
                    val isWebPage = nodes
                        .map { it.contentDescription }
                        .containsAll(
                            resources.getStringArray(R.array.web_page_buttons).toList()
                        )
                    val isSearch = event.text
                        .containsAll(
                            resources.getStringArray(R.array.search_page_buttons).toList()
                        )
                    if (isWebPage || isSearch) gotoHome()
                }
            }
        }
    }

    private fun gotoHome() {
        val intent = Intent()
        intent.action = "android.intent.action.MAIN"
        intent.addCategory("android.intent.category.HOME")
        intent.addFlags(
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                    or Intent.FLAG_ACTIVITY_FORWARD_RESULT
                    or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
                    or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        )
        startActivity(intent)
    }

    override fun onInterrupt() {}

    private fun startBlockNotificationService() {
        val serviceIntent = Intent(this, BlockNotificationService::class.java)
        startForegroundService(serviceIntent)
    }

    private fun stopBlockNotificationService() {
        val serviceIntent = Intent(this, BlockNotificationService::class.java)
        stopService(serviceIntent)
    }
}