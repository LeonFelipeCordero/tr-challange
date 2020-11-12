package com.tr.api

import com.tr.api.model.InstrumentPrice
import com.tr.api.service.InstrumentService
import org.reactivestreams.Publisher
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PriceController(private val instrumentService: InstrumentService) {

    @GetMapping("/instruments")
    fun getActiveInstruments(): Publisher<InstrumentPrice> = instrumentService.getCurrentPrices()
}
