package com.github.mrpingbot.telegram

import com.github.mrpingbot.telegram.dto.common.ChatType
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

            // todo добавить проверки на реакици после реализации https://github.com/tdlib/telegram-bot-api/issues/260
            ChatType.GROUP -> TelegramEvent.GROUP_MESSAGE
            else -> null
        }
    }
}
