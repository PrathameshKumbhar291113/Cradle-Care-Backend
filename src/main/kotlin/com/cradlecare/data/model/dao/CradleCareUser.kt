package com.cradlecare.data.model.dao

import io.ktor.server.auth.*

data class CradleCareUser(
    val userId :String?,
    val userName : String?,
    val userMobileNumber : Long?,
    val userPincode : Int?,
    val userDOB : String?,
    val userBloodGroup : String?,
    val userExpectedDeliveryDate : String?,
    val userIsKycDone : Boolean?,
    val userLastOtp : Int?,
    val userPanNumber : String?,
    val userAadharNumber : String?,
    val isUserLoggedIn: Boolean?,
    val isUserOnboarded: Boolean?
): Principal
