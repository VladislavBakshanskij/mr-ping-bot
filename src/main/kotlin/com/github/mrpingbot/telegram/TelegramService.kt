package com.github.mrpingbot.telegram

import com.github.mrpingbot.telegram.dto.common.TelegramMessage
import com.github.mrpingbot.telegram.dto.request.DeleteMessageRequest
import com.github.mrpingbot.telegram.dto.request.SendMessageRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class TelegramService(
    private val telegramClient: TelegramClient,
    @Value("\${telegram.code-review-chat-id:-835343898}") private val chatId: Long,
) {
    fun sendToCodeReviewChat(message: String): TelegramMessage = sendMessage(chatId, message)

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

    fun deleteMessage(
        messageId: Long,
        chatId: Long
    ) = telegramClient.deleteMessage(
        DeleteMessageRequest(
            messageId, chatId
        )
    )

}
