package com.tr.partner.client.wsc.handlers

import com.fasterxml.jackson.databind.ObjectMapper
import com.tr.model.PartnerInstrument
import com.tr.partner.client.rmqm.MessageSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InstrumentHandler(private val objectMapper: ObjectMapper,
                        private val messageSender: MessageSender) : MessageHandler {

    companion object {
        private val logger = LoggerFactory.getLogger(InstrumentHandler::class.java)
    }

    override fun handleMessage(message: String): PartnerInstrument {
        val instrument = objectMapper.readValue(message, PartnerInstrument::class.java)
        logger.info("update received for isin: ${instrument.data.isin}")
        processInstrument(instrument)
        return instrument
    }

    private fun processInstrument(partnerInstrument: PartnerInstrument) {
        messageSender.sendInstrumentUpdate(objectMapper.writeValueAsString(partnerInstrument))
    }
}
