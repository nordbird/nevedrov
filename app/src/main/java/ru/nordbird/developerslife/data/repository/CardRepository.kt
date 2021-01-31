package ru.nordbird.developerslife.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.nordbird.developerslife.data.api.ApiServiceImpl
import ru.nordbird.developerslife.data.model.Card
import ru.nordbird.developerslife.extensions.mutableLiveData
import ru.nordbird.developerslife.utils.Resource

object CardRepository {
    private val cardApi = ApiServiceImpl.getApi()

    private val cardsItems = mutableMapOf(
        CardCategory.LATEST to mutableLiveData(Resource.success(emptyList<Card>())),
        CardCategory.HOT to mutableLiveData(Resource.success(emptyList<Card>())),
        CardCategory.TOP to mutableLiveData(Resource.success(emptyList<Card>()))
    )
    private var page = mutableMapOf(
        CardCategory.LATEST to mutableLiveData(0),
        CardCategory.HOT to mutableLiveData(0),
        CardCategory.TOP to mutableLiveData(0)
    )

    fun getCards(category: CardCategory) = cardsItems[category]!!

    suspend fun loadNewCards(category: CardCategory) {
        withContext(Dispatchers.IO) {
            try {
                cardApi.getCards(category.path, page[category]!!.value!!).let {
                    if (it.isSuccessful) {
                        val newCards = it.body()?.result
                        val allCards = cardsItems[category]?.value?.data.orEmpty().toMutableList()

                        if (!newCards.isNullOrEmpty()) {
                            allCards.addAll(newCards.filter { card ->
                                !cardExists(category, card.id)
                            })
                        }

                        cardsItems[category]?.postValue(Resource.success(allCards))
                        page[category]!!.postValue(page[category]!!.value!! + 1)
                    } else cardsItems[category]?.postValue(Resource.error())
                }
            } catch (e: Exception) {
                cardsItems[category]?.postValue(Resource.error())
            }
        }
    }

    private fun cardExists(category: CardCategory, cardId: Int) =
        cardsItems[category]?.value!!.data?.find { it.id == cardId } != null

}

enum class CardCategory(val path: String) {
    LATEST("latest"),
    HOT("hot"),
    TOP("top")
}