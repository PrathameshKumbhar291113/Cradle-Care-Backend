package com.cradlecare.data.tables

import org.jetbrains.exposed.sql.Table

object CCUsersTable : Table() {

    val userId = varchar("userId", 6)
    val userName = varchar("userName", 512)
    val userMobileNumber = integer("userMobileNumber")
    val userPincode = integer("userPincode")
    val userDOB = long("userDOB")
    val userBloodGroup = varchar("userBloodGroup", 10)
    val userExpectedDeliveryDate = long("userExpectedDeliveryDate")
    val userIsKycDone = bool("userIsKycDone")

    override val primaryKey: Table.PrimaryKey = PrimaryKey(userId)
}