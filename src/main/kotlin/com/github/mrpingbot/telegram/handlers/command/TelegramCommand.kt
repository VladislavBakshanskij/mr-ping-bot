package com.github.mrpingbot.telegram.handlers.command

enum class TelegramCommand(val telegramCommandName: String) {
    START("start"),
    REGISTER_REVIEWER("register_reviewer"),
    UNDEFINED("")
}
