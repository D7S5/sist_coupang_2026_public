package com.skygames.goodpang.data.repository

import com.skygames.goodpang.data.model.Delivery
import com.skygames.goodpang.data.model.DeliveryCompleteResponse
import com.skygames.goodpang.data.network.DeliveryApiService
import com.skygames.goodpang.data.network.NetworkModule

class DeliveryRepository(
    private val api: DeliveryApiService = NetworkModule.apiService
) {
    suspend fun getDeliveries(): List<Delivery> = api.getDeliveries()

    suspend fun completeDelivery(deliveryNo: Int): DeliveryCompleteResponse =
        api.completeDelivery(deliveryNo)
}
