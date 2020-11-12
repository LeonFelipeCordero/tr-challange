package com.tr.partner.client.wsc

import com.tr.partner.client.wsc.handlers.InstrumentHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.net.URI
import javax.annotation.PostConstruct
import javax.websocket.ClientEndpoint

@Service
@ClientEndpoint
@Profile("prod")
class InstrumentsConnection(private val instrumentHandler: InstrumentHandler) : PartnerClient() {

    @Value("\${partner.host}")
    private lateinit var partnerHost: String

    companion object {
        private const val endpoint = "/instruments"
    }

    @PostConstruct
    fun init() {
        addMessageHandler(instrumentHandler)
        connect(URI(partnerHost + endpoint))
    }
}
