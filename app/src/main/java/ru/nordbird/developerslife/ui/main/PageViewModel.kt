package ru.nordbird.developerslife.ui.main

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.nordbird.developerslife.data.model.CardItem
import ru.nordbird.developerslife.data.repository.CardRepository
import ru.nordbird.developerslife.utils.Resource
import ru.nordbird.developerslife.utils.Status

class PageViewModel : ViewModel() {
    private val cardRepository = CardRepository

    private val _index = MutableLiveData<Int>()
    private val currentLatestIndex = MutableLiveData<Int>()


    fun setIndex(index: Int) {
        _index.value = index
    }

    private val latestItems = Transformations.map(cardRepository.getLatest()) { response ->
        val cards = response.data?.map { it.toCardItem() }
        return@map Resource(response.status, cards)
    }

    fun getCurrentIndex(): LiveData<Int> = currentLatestIndex

    fun getCurrent(): LiveData<Resource<CardItem>> {
        val result = MediatorLiveData<Resource<CardItem>>()

        val filterF = {
            val index = currentLatestIndex.value!!
            val cards = latestItems.value?.data

            val card = cards?.getOrNull(index)
            if (card != null) {
                result.value = Resource.success(card)
            } else {
                if (latestItems.value?.status == Status.ERROR) {
                    result.value = Resource.error()
                } else {
                    result.value = Resource.loading()
                    viewModelScope.launch {
                        cardRepository.loadNewCards()
                    }
                }
            }
        }

        result.addSource(latestItems) { filterF.invoke() }
        result.addSource(currentLatestIndex) { filterF.invoke() }

        return result
    }

    init {
        currentLatestIndex.value = 0
    }

    fun nextCard() {
        currentLatestIndex.value = currentLatestIndex.value!! + 1
    }

    fun prevCard() {
        currentLatestIndex.value = currentLatestIndex.value!! - 1
    }
}