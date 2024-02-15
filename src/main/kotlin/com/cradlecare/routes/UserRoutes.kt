package com.cradlecare.routes

import com.cradlecare.auth.CradleCareJwtService
import com.cradlecare.data.model.common_response.SimpleResponse
import com.cradlecare.data.model.dao.CradleCareUser
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

}