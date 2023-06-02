package com.github.mrpingbot.telegram.dto.request

// https://core.telegram.org/bots/api#sendmessage
data class SendMessageRequest(
    val chatId: Long,
    val text: String,
    val replyToMessageId: Long? = null,
    val allowSendingWithoutReply: Boolean = true,
    val disableNotification: Boolean = true,
)
