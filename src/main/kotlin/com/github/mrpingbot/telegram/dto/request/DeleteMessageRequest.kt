package com.github.mrpingbot.telegram.dto.request

data class DeleteMessageRequest (
    val messageId: Long,
    val chatId: Long
)
