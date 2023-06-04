package com.github.mrpingbot.telegram.handlers.command

import com.github.mrpingbot.telegram.dto.common.TelegramMessage
import org.springframework.stereotype.Component

@Component
class UndefinedTelegramCommandHandler : TelegramCommandHandler {
    override fun supportCommand(): TelegramCommand = TelegramCommand.UNDEFINED

    override fun handle(message: TelegramMessage): String = "Неизвестная команда"
}
