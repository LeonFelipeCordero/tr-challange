package com.tr.partner.client.rmqm

import org.springframework.amqp.core.FanoutExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessageExchangeConfiguration {

    @Bean
    fun instrumentsExchange(): FanoutExchange = FanoutExchange("instruments")

    @Bean
    fun quotesExchange(): FanoutExchange = FanoutExchange("quotes")

}
