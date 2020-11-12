package com.tr.api.rmqm

import org.springframework.amqp.core.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessageConfiguration {

    @Bean
    fun instrumentsExchange(): FanoutExchange = FanoutExchange("instruments")

    @Bean
    fun quotesExchange(): FanoutExchange = FanoutExchange("quotes")

    @Bean
    fun instrumentCacheQueue(): Queue = AnonymousQueue()

    @Bean
    fun quoteCacheQueue(): Queue = AnonymousQueue()

    @Bean
    fun instrumentDatabaseQueue(): Queue = AnonymousQueue()

    @Bean
    fun quotaDatabaseQueue(): Queue = AnonymousQueue()

    @Bean
    fun instrumentCacheBinding(instrumentsExchange: FanoutExchange, instrumentCacheQueue: Queue): Binding =
            BindingBuilder.bind(instrumentCacheQueue).to(instrumentsExchange)

    @Bean
    fun quotaCacheBinding(quotesExchange: FanoutExchange, quoteCacheQueue: Queue): Binding =
            BindingBuilder.bind(quoteCacheQueue).to(quotesExchange)

    @Bean
    fun instrumentDatabaseBinding(instrumentsExchange: FanoutExchange, instrumentDatabaseQueue: Queue): Binding =
            BindingBuilder.bind(instrumentDatabaseQueue).to(instrumentsExchange)

    @Bean
    fun quotaDatabaseBinding(quotesExchange: FanoutExchange, quotaDatabaseQueue: Queue): Binding =
            BindingBuilder.bind(quotaDatabaseQueue).to(quotesExchange)

}
