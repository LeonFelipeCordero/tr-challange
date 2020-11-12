package com.tr.api.service

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.tr.api.TestUtils.createInstrument
import com.tr.api.cache.InstrumentCache
import com.tr.api.model.Instrument
import com.tr.api.model.Status
import com.tr.api.repository.InstrumentRepository
import com.tr.model.InstrumentData
import com.tr.model.PartnerInstrument
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

internal class InstrumentServiceTest {

    companion object {
        private const val isin = "123"
        private const val description = "description"
    }

    @Mock
    private lateinit var instrumentRepository: InstrumentRepository

    private val instrumentCache: InstrumentCache = InstrumentCache()

    private lateinit var instrumentService: InstrumentService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        instrumentCache.setUp()

        instrumentService = InstrumentService(instrumentCache, instrumentRepository)
    }

    @AfterEach
    fun clear() {
        instrumentCache.setUp()
    }

    @Test
    fun `should save new instrument in cache`() {
        `when`(instrumentRepository.findFirstByIsin(isin)).thenReturn(Mono.empty())

        instrumentService.cacheInstrument(createNewPartnerInstrument("ADD"))

        val allInstruments = instrumentCache.getAllInstruments()

        assertThat(allInstruments).hasSize(1)
        assertThat(allInstruments[isin]).isEqualTo(-1.0)
        verifyNoInteractions(instrumentRepository)
    }

    @Test
    fun `should save new instrument in database`() {
        `when`(instrumentRepository.save(ArgumentMatchers.any(Instrument::class.java))).thenReturn(Mono.empty())

        `when`(instrumentRepository.findFirstByIsin(isin)).thenReturn(Mono.empty())

        instrumentService.saveInstrument(createNewPartnerInstrument("ADD"))

        verify(instrumentRepository).findFirstByIsin(isin)
        verify(instrumentRepository).save(ArgumentMatchers.any(Instrument::class.java))
        verifyNoMoreInteractions(instrumentRepository)
    }

    @Test
    fun `should reactivate instrument in databse`() {
        `when`(instrumentRepository.save(ArgumentMatchers.any(Instrument::class.java))).thenReturn(Mono.empty())

        `when`(instrumentRepository.findFirstByIsin(isin)).thenReturn(createInstrument("ARCHIVED"))

        instrumentService.saveInstrument(createNewPartnerInstrument("ADD"))

        verify(instrumentRepository).findFirstByIsin(isin)
        verify(instrumentRepository).save(createInstrument("ACTIVE").block()!!)
        verifyNoMoreInteractions(instrumentRepository)
    }

    @Test
    fun `should delete instrument from cache`() {
        `when`(instrumentRepository.findFirstByIsin(isin)).thenReturn(createInstrument("ACTIVE"))

        instrumentService.cacheInstrument(createNewPartnerInstrument("ADD"))
        instrumentService.cacheInstrument(createNewPartnerInstrument("DELETE"))

        val allInstruments = instrumentCache.getAllInstruments()

        assertThat(allInstruments).isEmpty()
    }

    @Test
    fun `should archive instrument in database`() {
        `when`(instrumentRepository.save(ArgumentMatchers.any(Instrument::class.java))).thenReturn(Mono.empty())
        `when`(instrumentRepository.findFirstByIsin(isin))
                .thenReturn(Mono.empty())
                .thenReturn(createInstrument("ACTIVE"))

        instrumentService.saveInstrument(createNewPartnerInstrument("ADD"))
        instrumentService.saveInstrument(createNewPartnerInstrument("DELETE"))

        verify(instrumentRepository, times(2)).findFirstByIsin(isin)
        verify(instrumentRepository, times(2)).save(ArgumentMatchers.any(Instrument::class.java))
        verifyNoMoreInteractions(instrumentRepository)
    }

    @Test
    fun `should throw exception when unknown type for cache`() {
        assertThatExceptionOfType(RuntimeException::class.java)
                .isThrownBy { instrumentService.cacheInstrument(createNewPartnerInstrument("TEST")) }
                .withMessage("Unknown action from partner API TEST")
    }

    @Test
    fun `should throw exception when unknown type`() {
        assertThatExceptionOfType(RuntimeException::class.java)
                .isThrownBy { instrumentService.saveInstrument(createNewPartnerInstrument("TEST")) }
                .withMessage("Unknown action from partner API TEST")
    }

    @Test
    fun `should throw exception when not present instrument on delete`() {
        `when`(instrumentRepository.findFirstByIsin(isin)).thenReturn(Mono.empty())

        assertThatExceptionOfType(RuntimeException::class.java)
                .isThrownBy { instrumentService.saveInstrument(createNewPartnerInstrument("DELETE")) }
                .withMessage("trying to archive an unknown instrument: $isin")
    }

    @Test
    fun `should return mapped prices correctly`() {
        savePricesInCache()
        val flux = instrumentService.getCurrentPrices()
        StepVerifier.create(flux.log())
                .assertNext {
                    assertThat(it.isin).isEqualTo("12345")
                    assertThat(it.price).isEqualTo("NULL")
                    assertThat(it.status).isEqualTo(Status.ACTIVE)
                }
                .assertNext {
                    assertThat(it.isin).isEqualTo("123")
                    assertThat(it.price).isEqualTo("1.0")
                    assertThat(it.status).isEqualTo(Status.ACTIVE)
                }
                .assertNext {
                    assertThat(it.isin).isEqualTo("1234")
                    assertThat(it.price).isEqualTo("2.0")
                    assertThat(it.status).isEqualTo(Status.ACTIVE)
                }
                .verifyComplete()
    }

    private fun savePricesInCache() {
        instrumentCache.updatePrice("123", 1.0)
        instrumentCache.updatePrice("1234", 2.0)
        instrumentCache.updatePrice("12345", -1.0)
    }

    private fun createNewPartnerInstrument(type: String): PartnerInstrument =
            PartnerInstrument(
                    data = InstrumentData(isin, description),
                    type = type
            )

}
