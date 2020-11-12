package com.tr.api.cache

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


internal class PartnerInstrumentCacheTest {

    companion object {
        private const val isin = "123"
        private const val price = 1.0
    }

    private lateinit var instrumentCache: InstrumentCache

    @BeforeEach
    fun setUp() {
        instrumentCache = InstrumentCache()
        instrumentCache.setUp()
    }

    @AfterEach
    fun after() {
        instrumentCache.deleteInstrument(isin)
    }

    @Test
    fun `should save new instrument with price in db and get it`() {
        instrumentCache.updatePrice(isin, price)
        val instrumentPrice = instrumentCache.getInstrumentPrice(isin)
        assertThat(instrumentPrice).isEqualTo(price)
    }

    @Test
    fun `should update instrument with bew price in db and get it`() {
        instrumentCache.updatePrice(isin, price)
        instrumentCache.updatePrice(isin, price + 1.1)
        val instrumentPrice = instrumentCache.getInstrumentPrice(isin)
        assertThat(instrumentPrice).isEqualTo(2.1)
    }

    @Test
    fun `should delete instrument from cache`() {
        instrumentCache.updatePrice(isin, price)
        instrumentCache.deleteInstrument(isin)
        assertThatExceptionOfType(RuntimeException::class.java)
                .isThrownBy { instrumentCache.getInstrumentPrice(isin) }
    }

    @Test
    fun `should get all instruments from cache`() {
        instrumentCache.updatePrice(isin, price)
        val instrumentsPrice = instrumentCache.getAllInstruments()
        assertThat(instrumentsPrice).hasSize(1)
    }
}
