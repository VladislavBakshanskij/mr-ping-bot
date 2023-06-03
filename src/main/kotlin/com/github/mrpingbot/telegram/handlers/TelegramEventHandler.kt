package com.github.mrpingbot.telegram.handlers

import com.github.mrpingbot.telegram.dto.request.UpdateRequest

interface TelegramEventHandler {
    fun supportEvent(): TelegramEvent

    fun handle(updateRequest: UpdateRequest)
}
