package com.dev.block.presentation.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dev.block.R
import com.dev.block.presentation.home.dialog.ConfirmPasswordDialog
import com.dev.block.presentation.home.dialog.SetPasswordDialog
import com.dev.block.ui.component.SwitchWithTitle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    navController: NavHostController,
    showLoading: (Boolean) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is HomeUiEvent.ToastEvent -> {
                    Toast.makeText(
                        context,
                        event.resId.fold("") { acc, id -> acc + context.getString(id) },
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            SwitchWithTitle(
                title = stringResource(id = R.string.block_start),
                checked = uiState.isSwitchOn,
                onCheckedChange = { isOn ->
                    if (isOn) viewModel.showSetPWDialog()
                    else viewModel.showConfirmPWDialog()
                }
            )
        }
    }

    if (uiState.showSetPassword) {
        SetPasswordDialog(
            onDismissRequest = { viewModel.dismissSetPWDialog() },
            onConfirmButtonClick = {
                viewModel.dismissSetPWDialog()
                viewModel.turnOn()
            },
            onRegisterEmailClick = { navController.navigate("registerEmail") },
            showLoading = showLoading
        )
    }

    if (uiState.showConfirmPassword) {
        ConfirmPasswordDialog(
            onDismissRequest = { viewModel.dismissConfirmPWDialog() },
            onConfirmButtonClick = {
                viewModel.dismissConfirmPWDialog()
                viewModel.turnOff()
            },
            showLoading = showLoading
        )
    }
}