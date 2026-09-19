package com.skygames.goodpang.ui.delivery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.skygames.goodpang.R
import com.skygames.goodpang.data.model.Delivery
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryScreen(
    modifier: Modifier = Modifier,
    viewModel: DeliveryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val completingDeliveryNos by viewModel.completingDeliveryNos.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var pendingConfirmation by remember { mutableStateOf<Delivery?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { message ->
            coroutineScope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.delivery_status_title)) },
                actions = {
                    IconButton(onClick = { viewModel.loadDeliveries() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.delivery_refresh))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when (val state = uiState) {
            is DeliveryUiState.Loading -> LoadingContent(innerPadding)
            is DeliveryUiState.Error -> ErrorContent(
                message = state.message,
                paddingValues = innerPadding,
                onRetry = { viewModel.loadDeliveries() }
            )
            is DeliveryUiState.Success -> {
                if (state.deliveries.isEmpty()) {
                    EmptyContent(innerPadding)
                } else {
                    DeliveryList(
                        deliveries = state.deliveries,
                        completingDeliveryNos = completingDeliveryNos,
                        paddingValues = innerPadding,
                        onCompleteClick = { delivery -> pendingConfirmation = delivery }
                    )
                }
            }
        }
    }

    pendingConfirmation?.let { delivery ->
        AlertDialog(
            onDismissRequest = { pendingConfirmation = null },
            title = { Text(stringResource(R.string.delivery_complete_confirm_title)) },
            text = { Text(stringResource(R.string.delivery_complete_confirm_message, delivery.productName)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.completeDelivery(delivery.deliveryNo)
                    pendingConfirmation = null
                }) {
                    Text(stringResource(R.string.delivery_confirm_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingConfirmation = null }) {
                    Text(stringResource(R.string.delivery_confirm_cancel))
                }
            }
        )
    }
}

@Composable
private fun DeliveryList(
    deliveries: List<Delivery>,
    completingDeliveryNos: Set<Int>,
    paddingValues: PaddingValues,
    onCompleteClick: (Delivery) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(deliveries, key = { it.deliveryNo }) { delivery ->
            DeliveryCard(
                delivery = delivery,
                isCompleting = delivery.deliveryNo in completingDeliveryNos,
                onCompleteClick = { onCompleteClick(delivery) }
            )
        }
    }
}

@Composable
private fun LoadingContent(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyContent(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.delivery_empty),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    paddingValues: PaddingValues,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = message, color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.delivery_retry))
            }
        }
    }
}
