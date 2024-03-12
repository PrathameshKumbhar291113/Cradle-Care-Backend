package com.cradlecare.data.model.request

data class UserRegisterRequest(
    val userName : String?,
    val userMobileNumber : Long?,
    val userLastOtp : Int?,
    val userPincode : Int?,
    val userDOB : String?,
    val userBloodGroup : String?,
    val expectedDeliveryDate : String?,
    val userIsKycDone : Boolean?,
    val userPanNumber : String?,
    val userAadharNumber : String?,
    val isUserLoggedIn: Boolean?,
    val isUserOnboarded: Boolean?
)
