package com.cradlecare.data.model.common_response

data class SimpleResponse<T>(
    val isSuccessFull : Boolean,
    val message: String,
    val response : T? = null
)
