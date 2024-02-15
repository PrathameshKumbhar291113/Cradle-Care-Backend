package com.cradlecare.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.cradlecare.auth.CradleCareJwtService
import com.cradlecare.repository.CradleCareUserRepository
import com.cradlecare.utils.CradleCareConstants.JWT_DECLARATION
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*

fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtAudience = "jwt-audience"
    val jwtDomain = "https://jwt-provider-domain/"
    val jwtRealm = "cradle care app"
    val jwtSecret = "secret"
   /* authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }*/

    val userDatabase = CradleCareUserRepository()

    data class MySession(val count: Int = 0)
    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    authentication {
        jwt (JWT_DECLARATION){
            realm = jwtRealm
            verifier(CradleCareJwtService().verifier)
            validate {
                val payload = it.payload
                val userId = payload.getClaim("userId").asString()
                val user = userDatabase.findUserByUserId(userId)
                user
            }
        }
    }


    /*routing {
        get("/session/increment") {
            val session = call.sessions.get<MySession>() ?: MySession()
            call.sessions.set(session.copy(count = session.count + 1))
            call.respondText("Counter is ${session.count}. Refresh to increment.")
        }
    }*/
}
