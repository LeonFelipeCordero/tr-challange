package com.tr.api.model

data class InstrumentPrice(val isin: String,
                           val status: Status,
                           val price: String)
