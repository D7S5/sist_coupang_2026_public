package com.skygames.goodpang.ui.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skygames.goodpang.data.repository.DeliveryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class DeliveryViewModel : ViewModel() {

    private val repository = DeliveryRepository()

    private val _uiState = MutableStateFlow<DeliveryUiState>(DeliveryUiState.Loading)
    val uiState: StateFlow<DeliveryUiState> = _uiState.asStateFlow()

    private val _completingDeliveryNos = MutableStateFlow<Set<Int>>(emptySet())
    val completingDeliveryNos: StateFlow<Set<Int>> = _completingDeliveryNos.asStateFlow()

    private val _messages = MutableSharedFlow<String>()
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    init {
        loadDeliveries()
    }

    fun loadDeliveries() {
        viewModelScope.launch {
            _uiState.value = DeliveryUiState.Loading
            _uiState.value = try {
                DeliveryUiState.Success(repository.getDeliveries())
            } catch (e: IOException) {
                DeliveryUiState.Error("네트워크 연결을 확인해주세요.")
            } catch (e: Exception) {
                DeliveryUiState.Error("배송 목록을 불러오지 못했습니다.")
            }
        }
    }

    fun completeDelivery(deliveryNo: Int) {
        val current = _uiState.value
        if (current !is DeliveryUiState.Success) return
        if (deliveryNo in _completingDeliveryNos.value) return

        viewModelScope.launch {
            _completingDeliveryNos.value = _completingDeliveryNos.value + deliveryNo
            try {
                val response = repository.completeDelivery(deliveryNo)
                if (response.success) {
                    val latest = _uiState.value
                    if (latest is DeliveryUiState.Success) {
                        _uiState.value = DeliveryUiState.Success(
                            latest.deliveries.filterNot { it.deliveryNo == deliveryNo }
                        )
                    }
                    _messages.emit("배송완료 처리되었습니다.")
                } else {
                    _messages.emit(response.message ?: "처리에 실패했습니다.")
                }
            } catch (e: IOException) {
                _messages.emit("네트워크 연결을 확인해주세요.")
            } catch (e: Exception) {
                _messages.emit("배송완료 처리 중 오류가 발생했습니다.")
            } finally {
                _completingDeliveryNos.value = _completingDeliveryNos.value - deliveryNo
            }
        }
    }
}
