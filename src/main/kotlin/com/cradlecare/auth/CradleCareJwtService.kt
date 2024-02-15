package com.cradlecare.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.cradlecare.data.model.dao.CradleCareUser

class CradleCareJwtService {
    private val issuer = "cradle_care_server"
    private val jwtSecret = System.getenv("CRADLE_CARE_JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    val verifier : JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(user: CradleCareUser) : String{
        return JWT.create()
            .withSubject("NoteAppAuth")
            .withIssuer(issuer)
            .withClaim("userId", user.userId)
            .sign(algorithm)
    }
}