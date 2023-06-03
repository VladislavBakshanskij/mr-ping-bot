package com.github.mrpingbot.message

class MessageNotFoundException(override val message: String) : RuntimeException(message) {
    companion object {
        fun ofId(id: Long): MessageNotFoundException = MessageNotFoundException("Сообщения с id $id не найдено")
    }
}