package com.devwujot.hashtag.core.data

class Resource<T> private constructor(val status: Status, var data: T?, val errorMessage: T?) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }
        fun <T> error(errorMessage: T?): Resource<T> {
            return Resource(Status.ERROR, null, errorMessage)
        }
        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}