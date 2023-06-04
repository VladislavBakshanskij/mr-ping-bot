package com.github.mrpingbot.telegram

import com.github.mrpingbot.telegram.dto.common.TelegramMessage
import com.github.mrpingbot.telegram.dto.request.SendMessageRequest
import org.springframework.stereotype.Service

@Service
class TelegramService(
    private val telegramClient: TelegramClient,
) {
    fun sendMessage(
        chatId: Long,
        message: String
    ): TelegramMessage = telegramClient.sendMessage(
        SendMessageRequest(
            chatId,
            message
        )
    ).result

    fun replyMessage(
        chatId: Long,
        messageId: Long,
        message: String
    ) = telegramClient.sendMessage(
        SendMessageRequest(
            chatId,
            message,
            messageId
        )
    ).result

}
