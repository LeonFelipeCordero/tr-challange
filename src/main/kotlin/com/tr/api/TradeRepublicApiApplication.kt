package com.tr.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
@EnableReactiveMongoRepositories("com.tr.api.repository")
class TradeRepublicApiApplication

fun main(args: Array<String>) {
    runApplication<TradeRepublicApiApplication>(*args)
}
