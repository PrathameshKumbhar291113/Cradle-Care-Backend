package com.cradlecare.data.model.request

data class UserLoginRequest(
    val userMobileNumber: Long,
    val userEnteredOtp: Int
)
