package ru.nordbird.developerslife.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ru.nordbird.developerslife.data.model.CardList

interface ApiService {

    @GET("latest/{page}?json=true")
    suspend fun getLatestCard(@Path("page") page: Int): Response<CardList>
}