package com.example.datastore.vo

sealed class Result<T> {

    data class Success<T>(val data: T) : Result<T>()

    data class Failure<T>(val message: String, val exception: Throwable? = null) : Result<T>()

}