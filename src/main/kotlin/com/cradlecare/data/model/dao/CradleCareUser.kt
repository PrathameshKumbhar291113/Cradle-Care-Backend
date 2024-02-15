package com.cradlecare.data.model.dao

import io.ktor.server.auth.*

data class CradleCareUser(
    val userId :String?,
    val userName : String?,
    val userMobileNumber : Long?,
    val userPincode : Int?,
    val userDOB : Long?,
    val userBloodGroup : String?,
    val userExpectedDeliveryDate : Long?,
    val userIsKycDone : Boolean?,
    val userLastOtp : Int?,
    val userPanNumber : String?,
    val userAadharNumber : String?
): Principal
