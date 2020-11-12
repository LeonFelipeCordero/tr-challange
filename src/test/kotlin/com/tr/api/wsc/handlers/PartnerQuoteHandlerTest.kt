//package com.tr.api.wsc.handlers
//
//import com.nhaarman.mockitokotlin2.any
//import com.nhaarman.mockitokotlin2.verify
//import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
//import com.tr.api.TestUtils.createInstrument
//import com.tr.api.TestUtils.fileToString
//import com.tr.api.cache.InstrumentCache
//import com.tr.api.config.AppConfig
//import com.tr.api.repository.InstrumentRepository
//import org.assertj.core.api.Assertions
//import org.assertj.core.api.Assertions.assertThat
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.Mock
//import org.mockito.Mockito.`when`
//import org.mockito.MockitoAnnotations
//
//internal class PartnerQuoteHandlerTest {
//
//    @Mock
//    private lateinit var instrumentRepository: InstrumentRepository
//
//    @Mock
//    private lateinit var instrumentCache: InstrumentCache
//
//    private lateinit var quoteHandler: QuoteHandler
//
//    @BeforeEach
//    fun setUp() {
//        MockitoAnnotations.initMocks(this)
//
//        `when`(instrumentRepository.findFirstByIsin("123441233")).thenReturn(createInstrument())
//
//        quoteHandler = QuoteHandler(AppConfig().objectMapper(), instrumentRepository, instrumentCache)
//    }
//
//    @Test
//    fun `should deserialize instrument message`() {
//        val message = fileToString("/mock/quote-message.json")
//        val instrument = quoteHandler.handleMessage(message)
//
//        assertThat(instrument.type).isEqualTo("QUOTE")
//        assertThat(instrument.data.isin).isEqualTo("123441233")
//        assertThat(instrument.data.price).isEqualTo(123.0)
//    }
//
//    @Test
//    fun `should save new instrument`() {
//        val message = fileToString("/mock/quote-message.json")
//        quoteHandler.handleMessage(message)
//
//        verify(instrumentCache).updatePrice("123441233", 123.0)
//        verifyNoMoreInteractions(instrumentCache)
//        verify(instrumentRepository).findFirstByIsin("123441233")
//    }
//
//    @Test
//    fun `should throw exception when unknown type`() {
//        val message = fileToString("/mock/unknown-quote-message.json")
//
//        Assertions.assertThatExceptionOfType(RuntimeException::class.java)
//                .isThrownBy { quoteHandler.handleMessage(message) }
//    }
//}
