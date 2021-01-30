package ru.nordbird.developerslife.data.api

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiServiceImpl {
    private const val BASE_URL = "https://developerslife.ru/"
    private var retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    fun getApi(): ApiService = retrofit.create(ApiService::class.java)
}