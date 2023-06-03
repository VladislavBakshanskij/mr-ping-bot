package com.github.mrpingbot.telegram.dto.common

data class TelegramMessage(
    val messageId: Long,
    val from: TelegramUser?,
    val chat: TelegramChat,
    val text: String?,
)