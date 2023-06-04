package com.github.mrpingbot.telegram.handlers.command

import com.github.mrpingbot.telegram.dto.common.TelegramMessage

interface TelegramCommandHandler {
    fun supportCommand(): TelegramCommand

    /**
     * После обработки сообщения в ответ отдается сообщение для пользователя
     */
    fun handle(message: TelegramMessage): String
}
