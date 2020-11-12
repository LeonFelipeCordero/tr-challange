package com.tr.api.service

import com.tr.api.cache.InstrumentCache
import com.tr.api.model.Instrument
import com.tr.api.model.InstrumentPrice
import com.tr.api.model.Status
import com.tr.api.repository.InstrumentRepository
import com.tr.model.PartnerInstrument
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class InstrumentService(private val instrumentCache: InstrumentCache,
                        private val instrumentRepository: InstrumentRepository) {

    companion object {
        private const val add = "ADD"
        private const val delete = "DELETE"

        private val logger = LoggerFactory.getLogger(InstrumentService::class.java)
    }

    fun getCurrentPrices(): Flux<InstrumentPrice> = Flux.fromStream(getInstrumentsStream())

    fun cacheInstrument(partnerInstrument: PartnerInstrument) {
        when (partnerInstrument.type) {
            add -> {
                instrumentCache.updatePrice(partnerInstrument.data.isin, -1.0)
            }
            delete -> {
                instrumentCache.deleteInstrument(partnerInstrument.data.isin)
            }
            else -> {
                throw RuntimeException("Unknown action from partner API ${partnerInstrument.type}")
            }
        }
    }

    fun saveInstrument(partnerInstrument: PartnerInstrument) {
        when (partnerInstrument.type) {
            add -> {
                persistInstrument(partnerInstrument)
            }
            delete -> {
                archiveInstrument(partnerInstrument)
            }
            else -> {
                throw RuntimeException("Unknown action from partner API ${partnerInstrument.type}")
            }
        }
    }

    private fun getInstrumentsStream() =
            instrumentCache.getAllInstruments()
                    .entries
                    .map { InstrumentPrice(isin = it.key, status = Status.ACTIVE, price = getPrice(it.value)) }
                    .stream()


    private fun getPrice(price: Double): String =
            if (price == -1.0) Status.NULL.toString()
            else price.toString()

    private fun persistInstrument(partnerInstrument: PartnerInstrument) {
        val instrument = instrumentRepository.findFirstByIsin(partnerInstrument.data.isin).block()
        if (instrument == null) {
            instrumentRepository.save(
                    Instrument(
                            isin = partnerInstrument.data.isin,
                            status = Status.ACTIVE,
                            description = partnerInstrument.data.description,
                            priceHistory = mutableListOf()
                    )
            ).subscribe()
        } else {
            instrument.status = Status.ACTIVE
            instrumentRepository.save(instrument).subscribe()
        }
    }

    private fun archiveInstrument(partnerInstrument: PartnerInstrument) {
        val instrument = instrumentRepository.findFirstByIsin(partnerInstrument.data.isin).block()
        if (instrument != null) {
            instrument.status = Status.ARCHIVED
            instrumentRepository.save(instrument).subscribe()
        } else {
            logger.error("trying to archive an unknown instrument: ${partnerInstrument.data.isin}")
        }
    }

}
