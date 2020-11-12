package com.tr.partner.client.rmqm

import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class MessageSender(private val rabbitTemplate: RabbitTemplate,
                    private val instrumentsExchange: FanoutExchange,
                    private val quotesExchange: FanoutExchange) {

    fun sendInstrumentUpdate(partnerInstrument: String) {
        rabbitTemplate.convertAndSend(instrumentsExchange.name, "", partnerInstrument)
    }

    fun sendQuoteUpdate(partnerQuote: String) {
        rabbitTemplate.convertAndSend(quotesExchange.name, "", partnerQuote)
    }
}
