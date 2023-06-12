package com.github.mrpingbot.telegram.handlers.command

import com.github.mrpingbot.telegram.dto.common.TelegramMessage
import com.github.mrpingbot.utils.getMessage
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component

@Component
class StartTelegramCommandHandler(private val messageSource: MessageSource) : TelegramCommandHandler {
    override fun supportCommand(): TelegramCommand = TelegramCommand.START

    override fun handle(
        message: TelegramMessage
    ): String = messageSource.getMessage("command.welcome")
}