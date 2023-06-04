package com.github.mrpingbot.telegram.handlers.command

import com.github.mrpingbot.telegram.dto.common.TelegramMessage
import org.springframework.stereotype.Component

@Component
class StartTelegramCommandHandler : TelegramCommandHandler {
    override fun supportCommand(): TelegramCommand = TelegramCommand.START

    override fun handle(
        message: TelegramMessage
    ): String = """
        ПРИВЕТСТВЕННОЕ СООБЩЕНИЕ
    """.trimIndent()
}