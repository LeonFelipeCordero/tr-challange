package com.tr.partner.client.wsc

import com.tr.partner.client.wsc.handlers.QuoteHandler
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.net.URI
import javax.annotation.PostConstruct
import javax.websocket.ClientEndpoint

@Service
@ClientEndpoint
@Profile("prod")
class QuoteConnection(private val quoteHandler: QuoteHandler) : PartnerClient() {

    @Value("\${partner.host}")
    private lateinit var partnerHost: String

    companion object {
        private const val endpoint = "/quotes"
    }

    @PostConstruct
    fun init() {
        addMessageHandler(quoteHandler)
        connect(URI(partnerHost + endpoint))
    }

}
