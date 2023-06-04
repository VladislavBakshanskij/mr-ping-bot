package com.github.mrpingbot.telegram.handlers

import com.github.mrpingbot.telegram.TelegramService
import com.github.mrpingbot.telegram.dto.request.UpdateRequest
import com.github.mrpingbot.telegram.handlers.command.TelegramCommand
import com.github.mrpingbot.telegram.handlers.command.TelegramCommandHandler
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component

@Component
class CommandTelegramEventHandler(
    private val telegramCommandHandlers: List<TelegramCommandHandler>,
    private val telegramService: TelegramService
) : TelegramEventHandler {
    override fun supportEvent(): TelegramEvent = TelegramEvent.COMMAND

    override fun handle(updateRequest: UpdateRequest) {
        val telegramMessage = updateRequest.message ?: return
        val telegramCommand = resolveCommand(telegramMessage.text!!) ?: TelegramCommand.UNDEFINED
        val telegramCommandHandler = telegramCommandHandlers.first { it.supportCommand() == telegramCommand }
        val message = telegramCommandHandler.handle(telegramMessage)
        telegramService.sendMessage(
            telegramMessage.chat.id,
            message
        )
    }

    private fun resolveCommand(messageText: String): TelegramCommand? {
        val command = messageText.split(StringUtils.SPACE).first()
        return TelegramCommand.values().find { command.contains(it.telegramCommandName) }
    }
}
