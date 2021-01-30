package ru.nordbird.developerslife.data.model

import com.squareup.moshi.Json

data class Card(
    @Json(name = "id")
    val id: Int,

    @Json(name = "description")
    val description: String,

    @Json(name = "gifURL")
    val gifURL: String
) {
    fun toCardItem() = CardItem(id, description, gifURL)
}
