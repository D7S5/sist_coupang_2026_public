package com.skygames.goodpang.ui.delivery

import com.skygames.goodpang.data.model.Delivery

sealed interface DeliveryUiState {
    data object Loading : DeliveryUiState
    data class Success(val deliveries: List<Delivery>) : DeliveryUiState
    data class Error(val message: String) : DeliveryUiState
}
