package ru.nordbird.developerslife.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import ru.nordbird.developerslife.data.model.CardList

interface ApiService {

    @GET("{category}/{page}?json=true")
    suspend fun getCards(
        @Path("category") category: String,
        @Path("page") page: Int
    ): Response<CardList>
}