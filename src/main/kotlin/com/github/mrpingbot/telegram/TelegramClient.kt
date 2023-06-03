package com.github.mrpingbot.telegram

import com.github.mrpingbot.telegram.dto.common.TelegramMessage
import com.github.mrpingbot.telegram.dto.request.SendMessageRequest
import com.github.mrpingbot.telegram.dto.response.SendMessageResponse

interface TelegramClient {
    fun sendMessage(request: SendMessageRequest): SendMessageResponse
}
