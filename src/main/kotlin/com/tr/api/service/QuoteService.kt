package com.tr.api.service

import com.tr.api.cache.InstrumentCache
import com.tr.api.model.Price
import com.tr.api.repository.InstrumentRepository
import com.tr.model.PartnerQuote
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.time.LocalDateTime

@Service
class QuoteService(private val instrumentCache: InstrumentCache,
                   private val instrumentRepository: InstrumentRepository) {

    companion object {
        private const val quote = "QUOTE"
    }

    fun processQuoteForCache(partnerQuote: PartnerQuote) {
        if (partnerQuote.type == quote) {
            instrumentCache.updatePrice(partnerQuote.data.isin, partnerQuote.data.price)
        } else {
            throw RuntimeException("Unknown quote type: ${partnerQuote.type}")
        }
    }

    fun saveQuote(partnerQuote: PartnerQuote) {
        if (partnerQuote.type == quote) {
            updatePriceHistory(partnerQuote)
        } else {
            throw RuntimeException("Unknown quote type: ${partnerQuote.type}")
        }
    }

    private fun updatePriceHistory(partnerQuote: PartnerQuote) {
        val instrument = instrumentRepository.findFirstByIsin(partnerQuote.data.isin).block()
        if (instrument != null) {
            instrument.addPrice(Price(partnerQuote.data.price, LocalDateTime.now()))
            instrumentRepository.save(instrument)
        } else {
            throw RuntimeException("trying to add a price to an unknown instrument: ${partnerQuote.data.isin}")
        }
    }
}
