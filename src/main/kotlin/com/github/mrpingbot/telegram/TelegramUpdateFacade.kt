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
            ChatType.PRIVATE -> TelegramEvent.PERSONAL_MESSAGE
            ChatType.GROUP -> {
                if (request.myChatMember != null) {
                    return if (request.myChatMember.newChatMember.status == ChatMemberStatus.MEMBER) {
                        TelegramEvent.JOIN_TO_GROUP
                    } else {
                        TelegramEvent.LEFT_FROM_GROUP
                    }
                }

                // todo добавить логику определения реакции
                return TelegramEvent.JOIN_TO_GROUP
            }

            else -> null
        }
    }
}
