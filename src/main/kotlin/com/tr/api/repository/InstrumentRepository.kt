package com.tr.api.repository

import com.tr.api.model.Instrument
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface InstrumentRepository : ReactiveMongoRepository<Instrument, String> {

    fun findFirstByIsin(isin: String): Mono<Instrument>
}
