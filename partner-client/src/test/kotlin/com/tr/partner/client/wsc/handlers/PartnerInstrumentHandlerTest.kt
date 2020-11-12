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

internal class PartnerInstrumentHandlerTest {

    @Mock
    private lateinit var messageSender: MessageSender

    private lateinit var instrumentHandler: InstrumentHandler

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        instrumentHandler = InstrumentHandler(AppConfig().objectMapper(), messageSender)
    }

    @Test
    fun `should deserialize instrument message`() {
        val message = fileToString("/mock/add-instrument-message.json")
        val instrument = instrumentHandler.handleMessage(message)

        assertThat(instrument.type).isEqualTo("ADD")
        assertThat(instrument.data.isin).isEqualTo("123441233")
        assertThat(instrument.data.description).isEqualTo("test")
    }

    @Test
    fun `should send new instrument`() {
        val message = fileToString("/mock/add-instrument-message.json")
        instrumentHandler.handleMessage(message)

        verify(messageSender).sendInstrumentUpdate(any())
        verifyNoMoreInteractions(messageSender)
    }
}
