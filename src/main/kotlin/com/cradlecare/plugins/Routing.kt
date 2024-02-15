package com.cradlecare.plugins

import com.cradlecare.auth.CradleCareJwtService
import com.cradlecare.auth.hash
import com.cradlecare.repository.CradleCareUserRepository
import com.cradlecare.routes.userRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        val userDatabase = CradleCareUserRepository()
        val jwtService = CradleCareJwtService()
        val hashFunc = { s: String -> hash(s) }

        userRoutes(userDatabase, jwtService, hashFunc)

    }
}
