package com.tr.api.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class Instrument(
        @Id val id: String? = null,
        val isin: String,
        var status: Status,
        var description: String,
        val priceHistory: MutableList<Price> = mutableListOf()
) {

    fun addPrice(price: Price) {
        priceHistory.add(price)
    }
}
