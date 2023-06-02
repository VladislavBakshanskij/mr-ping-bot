package com.github.mrpingbot.telegram.dto.request

//https://core.telegram.org/bots/api#getting-updates
data class UpdateRequest(
    val message: Message?,
    val myChatMember: ChatMemberUpdated?,
)

data class ChatMemberUpdated(
    val chat: Chat,
    val from: User,
    val newChatMember: ChatMember
)

data class ChatMember(
    val status: String,
    val user: User,
)

data class Message(
    val messageId: Long,
    val from: User,
    val chat: Chat,
    val text: String?,
)

data class User(
    val id: Long,
    val username: String?,
)

data class Chat(
    val id: Long
)
