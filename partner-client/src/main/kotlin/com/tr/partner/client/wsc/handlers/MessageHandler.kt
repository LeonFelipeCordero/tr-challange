package com.tr.partner.client.wsc.handlers

interface MessageHandler {
    fun handleMessage(message: String): Any
}
