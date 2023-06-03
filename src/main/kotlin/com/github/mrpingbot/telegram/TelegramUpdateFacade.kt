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
        val message = request.message ?: return

        // пока, что игнорируем сообщения из группы
        if (message.chat.type == ChatType.GROUP) {
            return
        }

        telegramEventHandlers.find { it.supportEvent() == TelegramEvent.PERSONAL_MESSAGE }?.handle(request)
    }
}
