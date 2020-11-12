package com.tr.partner.client.wsc

import com.tr.partner.client.wsc.handlers.MessageHandler
import org.slf4j.LoggerFactory
import java.net.URI
import javax.websocket.*

open class PartnerClient {

    private lateinit var messageHandler: MessageHandler
    private var session: Session? = null
    private val logger = LoggerFactory.getLogger(PartnerClient::class.java)

    protected fun connect(uri: URI) {
        val container = ContainerProvider.getWebSocketContainer()
        val session = container.connectToServer(this, uri)
        this.session = session
    }

    protected fun addMessageHandler(messageHandler: MessageHandler) {
        this.messageHandler = messageHandler
    }

    @OnClose
    fun onClose(userSession: Session, reason: CloseReason) {
        logger.info("closing websocket")
        session = null
    }

    @OnMessage
    fun onMessage(message: String) {
        this.messageHandler.handleMessage(message)
    }
}
