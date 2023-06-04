package com.github.mrpingbot.telegram

import com.github.mrpingbot.telegram.dto.common.ChatType
import com.github.mrpingbot.telegram.dto.request.ChatMemberStatus
import com.github.mrpingbot.telegram.dto.request.UpdateRequest
import com.github.mrpingbot.telegram.handlers.TelegramEvent
import com.github.mrpingbot.telegram.handlers.TelegramEventHandler
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TelegramUpdateFacade(
    private val telegramEventHandlers: List<TelegramEventHandler>
) {
    @Transactional
    fun handleUpdate(request: UpdateRequest) {
        val event: TelegramEvent? = resolveEvent(request)
        telegramEventHandlers.find { it.supportEvent() == event }?.handle(request)
    }

    private fun resolveEvent(request: UpdateRequest): TelegramEvent? {
        val message = request.message ?: return null
        return when (message.chat.type) {
            ChatType.PRIVATE -> {
                if (message.text != null && message.text.startsWith("/")) {
                    TelegramEvent.COMMAND
                } else {
                    TelegramEvent.PERSONAL_MESSAGE
                }
            }

            ChatType.GROUP -> {
                var event = TelegramEvent.JOIN_TO_GROUP
                if (request.myChatMember != null) {
                    event = if (request.myChatMember.newChatMember.status == ChatMemberStatus.MEMBER) {
                        TelegramEvent.JOIN_TO_GROUP
                    } else {
                        TelegramEvent.LEFT_FROM_GROUP
                    }
                }

                // todo добавить логику определения реакции
                return event
            }

            else -> null
        }
    }
}
