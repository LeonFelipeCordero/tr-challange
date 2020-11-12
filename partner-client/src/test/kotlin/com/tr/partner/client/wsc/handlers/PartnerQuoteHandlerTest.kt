package com.tr.partner.client.wsc.handlers

import com.tr.partner.client.TestUtils.fileToString
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.tr.partner.client.config.AppConfig
import com.tr.partner.client.rmqm.MessageSender
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

internal class PartnerQuoteHandlerTest {

    @Mock
    private lateinit var messageSender: MessageSender

    private lateinit var quoteHandler: QuoteHandler

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        quoteHandler = QuoteHandler(AppConfig().objectMapper(), messageSender)
    }

    @Test
    fun `should deserialize instrument message`() {
        val message = fileToString("/mock/quote-message.json")
        val instrument = quoteHandler.handleMessage(message)

        assertThat(instrument.type).isEqualTo("QUOTE")
        assertThat(instrument.data.isin).isEqualTo("123441233")
        assertThat(instrument.data.price).isEqualTo(123.0)
    }

    @Test
    fun `should save new quote price`() {
        val message = fileToString("/mock/quote-message.json")
        quoteHandler.handleMessage(message)

        verify(messageSender).sendQuoteUpdate(any())
        verifyNoMoreInteractions(messageSender)
    }
}
