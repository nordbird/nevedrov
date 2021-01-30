package ru.nordbird.developerslife.data.model

import com.squareup.moshi.Json

data class CardList(
    @Json(name = "result")
    val result: List<Card>
)
