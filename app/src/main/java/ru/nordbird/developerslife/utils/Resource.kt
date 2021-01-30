package ru.nordbird.developerslife.utils

data class Resource<out T>(val status: Status, val data: T?) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T> error(): Resource<T> {
            return Resource(Status.ERROR, null)
        }

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null)
        }

    }

}

enum class Status {
    SUCCESS,
    ERROR,
    LOADING
}