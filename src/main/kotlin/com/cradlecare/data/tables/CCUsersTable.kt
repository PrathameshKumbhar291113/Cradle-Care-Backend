package com.cradlecare.data.tables

import org.jetbrains.exposed.sql.Table

object CCUsersTable : Table() {

    val userId = varchar("userId", 6).nullable()
    val userName = varchar("userName", 512).nullable()
    val userMobileNumber = long("userMobileNumber").nullable()
    val userLastOtp = integer("userLastOtp").nullable()
    val userPincode = integer("userPincode").nullable()
    val userDOB = varchar("userDOB", 12).nullable()
    val userBloodGroup = varchar("userBloodGroup", 10).nullable()
    val userExpectedDeliveryDate = varchar("userExpectedDeliveryDate", 12).nullable()
    val userIsKycDone = bool("userIsKycDone").nullable().default(false)
    val userPanNumber = varchar("userPanNumber", 10).nullable()
    val userAadharNumber = varchar("userAadharNumber", 12).nullable()
    val isUserLoggedIn = bool("isUserLoggedIn").nullable().default(false)
    val isUserOnboarded = bool("isUserOnboarded").nullable().default(false)

    override val primaryKey: Table.PrimaryKey = PrimaryKey(userId)
}