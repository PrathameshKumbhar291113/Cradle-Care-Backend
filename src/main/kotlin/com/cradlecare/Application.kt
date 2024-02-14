package com.cradlecare

import com.cradlecare.plugins.*
import com.cradlecare.repository.DatabaseFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import javax.xml.crypto.Data

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.initializationOfCradleCareDb()
    configureSecurity()
    configureSerialization()
    configureRouting()
}
