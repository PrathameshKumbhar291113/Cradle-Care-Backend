package com.cradlecare.repository

import com.cradlecare.data.model.dao.CradleCareUser
import com.cradlecare.data.model.response.UserIsLoggedInOnboardedFlagResponse
import com.cradlecare.data.tables.CCUsersTable
import com.cradlecare.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class CradleCareUserRepository {

    suspend fun isUserMobileNumberExists(userMobileNumber: Long): Boolean = dbQuery {
        CCUsersTable.select { CCUsersTable.userMobileNumber.eq(userMobileNumber) }
            .count() > 0
    }

    suspend fun findUserByUserId(userId: String) = dbQuery {
        CCUsersTable.select { CCUsersTable.userId.eq(userId) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    suspend fun addUser(user: CradleCareUser) {
        dbQuery {
            CCUsersTable.insert { it ->
                it[CCUsersTable.userId] = user.userId
                it[CCUsersTable.userName] = user.userName
                it[CCUsersTable.userMobileNumber] = user.userMobileNumber
                it[CCUsersTable.userLastOtp] = user.userLastOtp
                it[CCUsersTable.userPincode] = user.userPincode
                it[CCUsersTable.userDOB] = user.userDOB
                it[CCUsersTable.userBloodGroup] = user.userBloodGroup
                it[CCUsersTable.userExpectedDeliveryDate] = userExpectedDeliveryDate
                it[CCUsersTable.userIsKycDone] = user.userIsKycDone
                it[CCUsersTable.userPanNumber] = user.userPanNumber
                it[CCUsersTable.userAadharNumber] = user.userAadharNumber
                it[CCUsersTable.isUserLoggedIn] = user.isUserLoggedIn
                it[CCUsersTable.isUserOnboarded] = user.isUserOnboarded
            }
        }
    }

    suspend fun isUserExists(userId: String): Boolean = dbQuery {
        CCUsersTable.select { CCUsersTable.userId.eq(userId) }
            .count() > 0
    }

    private fun rowToUser(row: ResultRow?): CradleCareUser? {
        if (row == null) {
            return null
        }

        return CradleCareUser(
            userId = row[CCUsersTable.userId],
            userName = row[CCUsersTable.userName],
            userMobileNumber = row[CCUsersTable.userMobileNumber],
            userLastOtp = row[CCUsersTable.userLastOtp],
            userPincode = row[CCUsersTable.userPincode],
            userDOB = row[CCUsersTable.userDOB],
            userBloodGroup = row[CCUsersTable.userBloodGroup],
            userExpectedDeliveryDate = row[CCUsersTable.userExpectedDeliveryDate],
            userIsKycDone = row[CCUsersTable.userIsKycDone],
            userPanNumber = row[CCUsersTable.userPanNumber],
            userAadharNumber = row[CCUsersTable.userAadharNumber],
            isUserLoggedIn = row[CCUsersTable.isUserLoggedIn],
            isUserOnboarded = row[CCUsersTable.isUserOnboarded]
        )
    }

    suspend fun updateIsUserLoggedInFlag(userId: String, isUserLoggedIn: Boolean) = dbQuery {
        CCUsersTable.update({ CCUsersTable.userId eq userId }) {
            it[CCUsersTable.isUserLoggedIn] = isUserLoggedIn
        }
    }

    suspend fun updateIsUserOnboardedFlag(userId: String, isUserOnboarded: Boolean) = dbQuery {
        CCUsersTable.update({ CCUsersTable.userId eq userId }) {
            it[CCUsersTable.isUserOnboarded] = isUserOnboarded
        }
    }

    suspend fun getIsUserLoggedInFlag(userId: String): UserIsLoggedInOnboardedFlagResponse = dbQuery {
        CCUsersTable.select { CCUsersTable.userId eq userId }
            .singleOrNull()?.let {
                UserIsLoggedInOnboardedFlagResponse(
                    isUserLoggedIn = it[CCUsersTable.isUserLoggedIn] ?: false,
                    isUserOnBoarded = it[CCUsersTable.isUserOnboarded] ?: false
                )
            } ?: throw NoSuchElementException("User not found for userId: $userId")
    }

    suspend fun getIsKycDoneStatus(userId: String): Boolean = dbQuery{
        CCUsersTable.select { CCUsersTable.userId eq userId }
            .map { it[CCUsersTable.userIsKycDone] }
            .singleOrNull() ?: false
    }

    suspend fun updateIsKycDoneFlag(userId: String, isKycDone: Boolean) = dbQuery {
        CCUsersTable.update({ CCUsersTable.userId eq userId }) {
            it[CCUsersTable.userIsKycDone] = isKycDone
        }
    }

    suspend fun getDaysLeftForDelivery(userId: String): Long? = dbQuery {
        val resultRow = CCUsersTable.select { CCUsersTable.userId eq userId }
            .singleOrNull()

        val expectedDeliveryDateString = resultRow?.getOrNull(CCUsersTable.userExpectedDeliveryDate) ?: return@dbQuery null

        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val expectedDeliveryDate = LocalDate.parse(expectedDeliveryDateString, formatter)
        val currentDate = LocalDate.now()

        return@dbQuery ChronoUnit.DAYS.between(currentDate, expectedDeliveryDate)
    }

}