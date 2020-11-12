package com.tr.api

import com.tr.api.model.Instrument
import com.tr.api.model.Status
import reactor.core.publisher.Mono
import java.nio.file.Files
import java.nio.file.Paths

object TestUtils {

    fun fileToString(filePath: String): String =
            Files.readString(Paths.get(this.javaClass.getResource(filePath).toURI()))

    fun createInstrument(status: String): Mono<Instrument> =
            Mono.just(
                    Instrument(
                            isin = "123441233",
                            status = Status.ACTIVE,
                            description = "description",
                            priceHistory = mutableListOf()
                    )
            )
}
