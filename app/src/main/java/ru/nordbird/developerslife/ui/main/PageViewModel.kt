package ru.nordbird.developerslife.ui.main

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.nordbird.developerslife.data.model.CardItem
import ru.nordbird.developerslife.data.repository.CardCategory
import ru.nordbird.developerslife.data.repository.CardRepository
import ru.nordbird.developerslife.extensions.mutableLiveData
import ru.nordbird.developerslife.utils.Resource
import ru.nordbird.developerslife.utils.Status

class PageViewModelFactory(private val pageType: PageType) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = PageViewModel(pageType) as T
}

class PageViewModel(private val pageType: PageType) : ViewModel() {
    private val cardRepository = CardRepository

    private val cardIndex = mutableLiveData(0)

    private val cardItems =
        Transformations.map(cardRepository.getCards(pageType.category)) { response ->
            val cards = response.data?.map { it.toCardItem() }
            return@map Resource(response.status, cards)
        }

    fun getCardIndex(): LiveData<Int> = cardIndex

    fun getCard(): LiveData<Resource<CardItem>> {
        val result = MediatorLiveData<Resource<CardItem>>()

        val filterF = {
            val index = cardIndex.value!!
            val cards = cardItems.value?.data

            val card = cards?.getOrNull(index)
            if (card != null) {
                result.value = Resource.success(card)
            } else {
                if (cardItems.value?.status == Status.ERROR) {
                    result.value = Resource.error()
                } else {
                    result.value = Resource.loading()
                    viewModelScope.launch {
                        cardRepository.loadNewCards(pageType.category)
                    }
                }
            }
        }

        result.addSource(cardItems) { filterF.invoke() }
        result.addSource(cardIndex) { filterF.invoke() }

        return result
    }

    fun nextCard() {
        cardIndex.value = cardIndex.value!! + 1
    }

    fun prevCard() {
        cardIndex.value = cardIndex.value!! - 1
    }

    fun reloadCard() {
        viewModelScope.launch {
            cardRepository.loadNewCards(pageType.category)
        }
    }

}

enum class PageType(val category: CardCategory) {
    LATEST(CardCategory.LATEST),
    HOT(CardCategory.HOT),
    TOP(CardCategory.TOP)
}