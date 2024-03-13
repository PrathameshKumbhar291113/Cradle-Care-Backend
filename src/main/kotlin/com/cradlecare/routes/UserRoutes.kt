package com.cradlecare.routes

import com.cradlecare.auth.CradleCareJwtService
import com.cradlecare.data.model.common_response.SimpleResponse
import com.cradlecare.data.model.dao.CradleCareUser
import com.cradlecare.data.model.request.UserFlagsForLoggedIn
import com.cradlecare.data.model.request.UserFlagsForOnboarded
import com.cradlecare.data.model.request.UserLoginRequest
import com.cradlecare.data.model.request.UserRegisterRequest
import com.cradlecare.data.model.response.UserLoginResponse
import com.cradlecare.data.model.response.UserRegisterResponse
import com.cradlecare.repository.CradleCareUserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val API_VERSION = "/v1"
const val USERS = "$API_VERSION/users"
const val REGISTER_REQUEST = "$USERS/register"
const val LOGIN_REQUEST = "$USERS/login"
const val CHECK_SERVER_RUNNING ="$USERS/checkHello"
const val POST_IS_USER_LOGGED_IN = "$USERS/postUserLoggedIn"
const val POST_IS_USER_ONBOARDED = "$USERS/postUserOnboarded"
const val GET_IS_USER_LOGGED_IN_AND_ONBOARDED = "$USERS/getUserLoggedInAndOnboarded"
const val GET_IS_KYC_DONE = "$USERS/getIsKycDone"
const val POST_IS_KYC_DONE = "$USERS/postIsKycDone"
const val GET_DAYS_LEFT_FOR_PREGNANCY = "$USERS/getDaysLeftForPregnancy"

fun Route.userRoutes(
    ccUserRepo: CradleCareUserRepository,
    ccJwtService: CradleCareJwtService,
    hashFunction: (String) -> String
) {


    post(REGISTER_REQUEST) {
        val registerRequest = try {
            call.receive<UserRegisterRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Some Fields", response = null))
            return@post
        }

        try {

            if (ccUserRepo.isUserMobileNumberExists(registerRequest.userMobileNumber!!)) {
                call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "User Already Exist.", response = null))
            } else {
                var userId: String = ""

                do {
                    userId = (100000..999999).random().toString()
                } while (ccUserRepo.isUserExists(userId))

                val user = CradleCareUser(
                    userId,
                    registerRequest.userName,
                    hashFunction(registerRequest.userMobileNumber.toString()).trim().toLong(),
                    registerRequest.userPincode,
                    registerRequest.userDOB,
                    registerRequest.userBloodGroup,
                    registerRequest.expectedDeliveryDate,
                    registerRequest.userIsKycDone,
                    registerRequest.userLastOtp,
                    registerRequest.userPanNumber,
                    registerRequest.userAadharNumber,
                    registerRequest.isUserLoggedIn,
                    registerRequest.isUserOnboarded
                )

                /*val userResponseWithoutPassword = UserResponseWithoutPassword(userName = user.userName, userId = user.userId , userEmail = user.userEmail)*/

                val registerResponse = UserRegisterResponse(token = ccJwtService.generateToken(user))

                ccUserRepo.addUser(user)
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(
                        isSuccessFull = true,
                        message = "User Successfully Registered.",
                        response = registerResponse
                    )
                )
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, e.message ?: "Some error occurred.", response = null)
            )
        }
    }

    post(LOGIN_REQUEST) {
        val login = try {
            call.receive<UserLoginRequest>()
        } catch (e: Exception) {
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Some Fields", response = null))
            return@post
        }

        try {
            val user = ccUserRepo.isUserMobileNumberExists(login.userMobileNumber)
            if (user) {
                call.respond(
                    HttpStatusCode.OK,
                    SimpleResponse(isSuccessFull = true, "Successfully Login.", response = UserLoginResponse(userExist = true))
                )
            } else {

                call.respond(
                    HttpStatusCode.BadRequest,
                    SimpleResponse(true, "User Doesn't Exist.", response = UserLoginResponse(userExist = false))
                )
            }
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, e.message ?: "Some error occurred.", response = null)
            )
        }

    }

    post(POST_IS_USER_LOGGED_IN) {
        val isUserLoggedIn = try{
            call.receive<UserFlagsForLoggedIn>()
        }catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Some Fields", response = null))
            return@post
        }

        try {
            ccUserRepo.updateIsUserLoggedInFlag(isUserLoggedIn.userId , isUserLoggedIn.userLoggedIn)
            call.respond(
                HttpStatusCode.OK,
                SimpleResponse(true, "Is User Logged In Flag Updated Successfully.", response = null)
            )
        }catch (e: Exception){
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, e.message ?: "Some error occurred.", response = null)
            )
        }
    }

    post(POST_IS_USER_ONBOARDED) {
        val isUserOnboarded = try{
            call.receive<UserFlagsForOnboarded>()
        }catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Some Fields", response = null))
            return@post
        }

        try {
            ccUserRepo.updateIsUserOnboardedFlag(isUserOnboarded.userId , isUserOnboarded.userOnBoarded)
            call.respond(
                HttpStatusCode.OK,
                SimpleResponse(true, "Is User Onboarded Flag Updated Successfully.", response = null)
            )
        }catch (e: Exception){
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, e.message ?: "Some error occurred.", response = null)
            )
        }
    }

    get (GET_IS_USER_LOGGED_IN_AND_ONBOARDED){
        val userId = try {
            call.request.queryParameters["userId"]
        }catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"QueryParameter : userId is not present", response = null))
            return@get
        }

        try {
            userId?.let {
                val isUserLoggedInAndOnboardedFlag = ccUserRepo.getIsUserLoggedInFlag(it)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "User Is LoggedIn And Onboarded Flags Sent Successfully!", response = isUserLoggedInAndOnboardedFlag))
            }

        }catch (e: Exception){
            call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message ?: "Some problem Occurred!", response = null))
        }
    }

    get (GET_IS_KYC_DONE){
        val userId = try {
            call.request.queryParameters["userId"]
        }catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest,SimpleResponse(false,"QueryParameter : userId is not present", response = null))
            return@get
        }

        try {
            userId?.let {
                val isKycDone = ccUserRepo.getIsKycDoneStatus(it)
                call.respond(HttpStatusCode.OK, SimpleResponse(true, "Is Kyc Done Flag Sent Successfully!", response = isKycDone))
            }

        }catch (e: Exception){
            call.respond(HttpStatusCode.Conflict,SimpleResponse(false, e.message ?: "Some problem Occurred!", response = null))
        }
    }

    post(POST_IS_KYC_DONE){

        val isKycDone = try{
            call.receive<UserFlagsForOnboarded>()
        }catch (e: Exception){
            call.respond(HttpStatusCode.BadRequest, SimpleResponse(false, "Missing Some Fields", response = null))
            return@post
        }

        try {
            ccUserRepo.updateIsKycDoneFlag(isKycDone.userId , isKycDone.userOnBoarded)
            call.respond(
                HttpStatusCode.OK,
                SimpleResponse(true, "Is User Kyc Done Flag Updated Successfully.", response = null)
            )
        }catch (e: Exception){
            call.respond(
                HttpStatusCode.Conflict,
                SimpleResponse(false, e.message ?: "Some error occurred.", response = null)
            )
        }

    }


    get(GET_DAYS_LEFT_FOR_PREGNANCY) {
        
    }

    get(CHECK_SERVER_RUNNING){
        call.respondText { "SERVER WORKING" }
    }

}