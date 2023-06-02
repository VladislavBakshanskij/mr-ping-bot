package com.github.mrpingbot.telegram

import com.github.mrpingbot.telegram.dto.request.SendMessageRequest

interface TelegramClient {
    fun sendMessage(request: SendMessageRequest)
}
