package com.tr.api.service

import com.nhaarman.mockitokotlin2.verify
import com.tr.api.TestUtils.createInstrument
import com.tr.api.cache.InstrumentCache
import com.tr.api.repository.InstrumentRepository
import com.tr.model.PartnerQuote
import com.tr.model.QuoteData
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import reactor.core.publisher.Mono
import java.lang.RuntimeException

internal class QuoteServiceTest {

    companion object {
        private const val isin = "123"
        private const val price = .1
    }

    @Mock
    private lateinit var instrumentRepository: InstrumentRepository

    private val instrumentCache: InstrumentCache = InstrumentCache()

    private lateinit var quoteService: QuoteService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        instrumentCache.setUp()

        quoteService = QuoteService(instrumentCache, instrumentRepository)
    }

    @AfterEach
    fun clear() {
        instrumentCache.deleteInstrument(isin)
    }

    @Test
    fun `should add quote to cache`() {
        `when`(instrumentRepository.findFirstByIsin(isin)).thenReturn(createInstrument("ACTIVE"))

        quoteService.processQuoteForCache(createPartnerQuote("QUOTE"))

        val allInstruments = instrumentCache.getAllInstruments()

        assertThat(allInstruments[isin]).isEqualTo(price)
    }

    @Test
    fun `should add quote to instrument history`() {
        val instrument = createInstrument("ACTIVE")

        `when`(instrumentRepository.findFirstByIsin(isin)).thenReturn(instrument)

        quoteService.processQuoteForCache(createPartnerQuote("QUOTE"))

        assertThat(instrument.block()!!.priceHistory[0].price).isEqualTo(price)
        assertThat(instrument.block()!!.priceHistory[0].timestamp).isNotNull()
        verify(instrumentRepository).findFirstByIsin(isin)
        verify(instrumentRepository).save(instrument.block()!!)
    }

    @Test
    fun `should throw exception when type is not known`() {
        assertThatExceptionOfType(RuntimeException::class.java)
                .isThrownBy { quoteService.processQuoteForCache(createPartnerQuote("TEST")) }
                .withMessage("Unknown quote type: TEST")
    }

    @Test
    fun `should throw exception when instrument is not on present`() {
        `when`(instrumentRepository.findFirstByIsin(isin)).thenReturn(Mono.empty())

        assertThatExceptionOfType(RuntimeException::class.java)
                .isThrownBy { quoteService.processQuoteForCache(createPartnerQuote("QUOTE")) }
                .withMessage("trying to add a price to an unknown instrument: $isin")
    }

    private fun createPartnerQuote(type: String): PartnerQuote =
            PartnerQuote(
                    QuoteData(price, isin),
                    type
            )

}
