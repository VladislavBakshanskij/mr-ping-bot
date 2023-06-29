package com.github.mrpingbot.telegram.dto.request

import com.github.mrpingbot.telegram.dto.common.TelegramChat
import com.github.mrpingbot.telegram.dto.common.TelegramMessage
import com.github.mrpingbot.telegram.dto.common.TelegramUser

//https://core.telegram.org/bots/api#getting-updates
data class UpdateRequest(
    val message: TelegramMessage?,
    val myChatMember: ChatMemberUpdated?,
    val editedMessage: TelegramMessage?,
)

data class ChatMemberUpdated(
    val chat: TelegramChat,
    val from: TelegramUser,
    val newChatMember: ChatMember
)

data class ChatMember(
    val status: ChatMemberStatus,
    val user: TelegramUser,
)

enum class ChatMemberStatus {
    LEFT,
    MEMBER,
    ADMINISTRATOR,
    OWNER,
    BOT
}

