package com.skygames.goodpang.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    const val BASE_URL = "http://scym3.cafe24.com:8080/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
            .build()
    }

    val apiService: DeliveryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeliveryApiService::class.java)
    }

    fun resolveImageUrl(path: String): String {
        if (path.startsWith("http://") || path.startsWith("https://")) return path
        return BASE_URL + path.removePrefix("/")
    }
}
