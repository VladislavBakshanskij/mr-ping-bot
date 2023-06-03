package com.github.mrpingbot.message

interface MessageRepository {
    fun save(message: Message): Message

    fun findById(id: Long): Message?
}
