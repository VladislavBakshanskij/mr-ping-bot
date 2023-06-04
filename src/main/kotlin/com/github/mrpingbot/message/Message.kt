package com.github.mrpingbot.message

data class Message(
    val id: Long,
    val chatId: Long,
    val text: String?,
)