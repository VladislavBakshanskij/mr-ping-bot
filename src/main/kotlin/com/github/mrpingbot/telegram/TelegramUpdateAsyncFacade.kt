package com.github.mrpingbot.telegram

import com.github.mrpingbot.telegram.dto.request.UpdateRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class TelegramUpdateAsyncFacade(
    private val telegramUpdateFacade: TelegramUpdateFacade
) {
    @Async
    fun handleUpdate(request: UpdateRequest) = telegramUpdateFacade.handleUpdate(request)
}
