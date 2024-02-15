package com.cradlecare.data.model.request

data class UserRegisterRequest(
    val userName : String?,
    val userMobileNumber : Long?,
    val userLastOtp : Int?,
    val userPincode : Int?,
    val userDOB : Long?,
    val userBloodGroup : String?,
    val expectedDeliveryDate : Long?,
    val userIsKycDone : Boolean?,
    val userPanNumber : String?,
    val userAadharNumber : String?
)
