package com.skygames.goodpang.data.network

import com.skygames.goodpang.data.model.Delivery
import com.skygames.goodpang.data.model.DeliveryCompleteResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface DeliveryApiService {

    @GET("deliveries/json")
    suspend fun getDeliveries(): List<Delivery>

    @FormUrlEncoded
    @POST("delivery-complete/json")
    suspend fun completeDelivery(@Field("deliveryNo") deliveryNo: Int): DeliveryCompleteResponse
}
