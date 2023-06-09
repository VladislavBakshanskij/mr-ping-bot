package com.github.mrpingbot.telegram

import com.github.mrpingbot.telegram.dto.request.DeleteMessageRequest
import com.github.mrpingbot.telegram.dto.request.SendMessageRequest
import com.github.mrpingbot.telegram.dto.response.SendMessageResponse

interface TelegramClient {
    fun sendMessage(request: SendMessageRequest): SendMessageResponse

    fun deleteMessage(request: DeleteMessageRequest)
}
