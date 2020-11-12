package com.tr.api.rmqm

import com.fasterxml.jackson.databind.ObjectMapper
import com.tr.api.service.InstrumentService
import com.tr.api.service.QuoteService
import com.tr.model.PartnerInstrument
import com.tr.model.PartnerQuote
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class MessageReceiver(private val objectMapper: ObjectMapper,
                      private val instrumentService: InstrumentService,
                      private val quoteService: QuoteService) {

    companion object {
        private val logger = LoggerFactory.getLogger(MessageReceiver::class.java)
    }

    @RabbitListener(queues = ["#{instrumentCacheQueue.name}"])
    fun instrumentCacheReceiver(message: String) {
        logger.info("for cache $message")
        val instrument = objectMapper.readValue(message, PartnerInstrument::class.java)
        instrumentService.cacheInstrument(instrument)
    }

    @RabbitListener(queues = ["#{quoteCacheQueue.name}"])
    fun quoteCacheReceiver(message: String) {
        val quote = objectMapper.readValue(message, PartnerQuote::class.java)
        quoteService.processQuoteForCache(quote)
    }

    @RabbitListener(queues = ["#{instrumentDatabaseQueue.name}"])
    fun instrumentDatabaseReceiver(message: String) {
        logger.info("for database $message")
        val instrument = objectMapper.readValue(message, PartnerInstrument::class.java)
        instrumentService.saveInstrument(instrument)
    }

    //    @RabbitListener(queues = ["#{quotaDatabaseQueue.name}"])
    fun quoteDatabaseReceiver(message: String) {
        val quote = objectMapper.readValue(message, PartnerQuote::class.java)
    }
}
