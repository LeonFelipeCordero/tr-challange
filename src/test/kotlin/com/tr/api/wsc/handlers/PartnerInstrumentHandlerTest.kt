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
//import org.assertj.core.api.Assertions.assertThat
//import org.assertj.core.api.Assertions.assertThatExceptionOfType
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.mockito.Mock
//import org.mockito.Mockito.`when`
//import org.mockito.MockitoAnnotations
//
//internal class PartnerInstrumentHandlerTest {
//
//    @Mock
//    private lateinit var instrumentRepository: InstrumentRepository
//
//    @Mock
//    private lateinit var instrumentCache: InstrumentCache
//
//    private lateinit var instrumentHandler: InstrumentHandler
//
//    @BeforeEach
//    fun setUp() {
//        MockitoAnnotations.initMocks(this)
//
//        `when`(instrumentRepository.findFirstByIsin("123441233")).thenReturn(createInstrument())
//
//        instrumentHandler = InstrumentHandler(AppConfig().objectMapper(), instrumentRepository, instrumentCache)
//    }
//
//    @Test
//    fun `should deserialize instrument message`() {
//        val message = fileToString("/mock/add-instrument-message.json")
//        val instrument = instrumentHandler.handleMessage(message)
//
//        assertThat(instrument.type).isEqualTo("ADD")
//        assertThat(instrument.data.isin).isEqualTo("123441233")
//        assertThat(instrument.data.description).isEqualTo("test")
//    }
//
//    @Test
//    fun `should save new instrument`() {
//        val message = fileToString("/mock/add-instrument-message.json")
//        instrumentHandler.handleMessage(message)
//
//        verify(instrumentCache).updatePrice("123441233", -1.0)
//        verifyNoMoreInteractions(instrumentCache)
//        verify(instrumentRepository).findFirstByIsin("123441233")
//    }
//
//    @Test
//    fun `should delete new instrument`() {
//        val message = fileToString("/mock/delete-instrument-message.json")
//        instrumentHandler.handleMessage(message)
//
//        verify(instrumentCache).deleteInstrument("123441233")
//        verifyNoMoreInteractions(instrumentCache)
//        verify(instrumentRepository).findFirstByIsin("123441233")
//    }
//
//    @Test
//    fun `should throw exception when unknown type`() {
//        val message = fileToString("/mock/unknown-instrument-message.json")
//
//        assertThatExceptionOfType(RuntimeException::class.java)
//                .isThrownBy { instrumentHandler.handleMessage(message) }
//    }
//}
