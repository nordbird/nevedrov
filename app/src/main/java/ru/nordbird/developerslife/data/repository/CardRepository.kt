package ru.nordbird.developerslife.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nordbird.developerslife.data.api.ApiServiceImpl
import ru.nordbird.developerslife.data.model.Card
import ru.nordbird.developerslife.extensions.mutableLiveData
import ru.nordbird.developerslife.utils.Resource

object CardRepository {
    private val cardApi = ApiServiceImpl.getApi()

    private val latestItems = mutableLiveData(Resource.success(emptyList<Card>()))
    private var page = 0

    fun getLatest() = latestItems

    suspend fun loadNewCards() {
        withContext(Dispatchers.IO) {
            try {
                cardApi.getLatestCard(page).let {
                    if (it.isSuccessful) {
                        val newCards = it.body()?.result
                        val allCards = latestItems.value?.data.orEmpty().toMutableList()

                        if (!newCards.isNullOrEmpty()) {
                            allCards.addAll(newCards.filter { card -> !cardExists(card.id) })
                        }

                        latestItems.postValue(Resource.success(allCards))
                        page++
                    } else latestItems.postValue(Resource.error())
                }
            } catch (e: Exception) {
                latestItems.postValue(Resource.error())
            }
        }
    }

    private fun cardExists(cardId: Int) = latestItems.value!!.data?.find { it.id == cardId } != null

}