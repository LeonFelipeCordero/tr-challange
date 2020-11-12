package com.tr.partner.client.wsc.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import com.tr.model.PartnerQuote
import com.tr.partner.client.rmqm.MessageSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class QuoteHandler(private val objectMapper: ObjectMapper,
                   private val messageSender: MessageSender) : MessageHandler {

    companion object {
        private val logger = LoggerFactory.getLogger(QuoteHandler::class.java)
    }

    override fun handleMessage(message: String): PartnerQuote {
        val quote = objectMapper.readValue(message, PartnerQuote::class.java)
        logger.info("New price " + quote.data.price + " for ISIN " + quote.data.isin + " received")
        processQuote(quote)
        return quote
    }

    private fun processQuote(partnerQuote: PartnerQuote) {
        messageSender.sendQuoteUpdate(objectMapper.writeValueAsString(partnerQuote))
    }
}
