package com.cradlecare.repository

import com.cradlecare.data.tables.CCUsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun initializationOfCradleCareDb(){
        Database.connect(hikari())

        transaction {
            SchemaUtils.create(CCUsersTable)
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = "jdbc:postgresql:cradle_care_db?user=postgres&password=Pratham13"
        config.maximumPoolSize = 3
        config.isAutoCommit = true
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

//        val uri = URI(System.getenv("DATABASE_URL"))
//        val userName = uri.userInfo.split(":").toTypedArray()[0]
//        val password = uri.userInfo.split(":").toTypedArray()[1]
//
//        config.jdbcUrl = "jdbc:postgresql://"+uri.host + ":" + uri.port + uri.path + "?sslmode=require" + "&user=$userName&password=$password"

        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T) :T =
        withContext(Dispatchers.IO){
            transaction{block()}
        }

}