package com.github.mrpingbot.message

import org.springframework.stereotype.Service

@Service
class MessageService(
    private val messageRepository: MessageRepository
) {
    fun createIfNotExists(message: Message): Message {
        val foundedMessage = messageRepository.findById(message.id)
        return foundedMessage ?: messageRepository.save(message)
    }

    fun getById(messageId: Long): Message =
        messageRepository.findById(messageId) ?: throw MessageNotFoundException.ofId(messageId)
}
