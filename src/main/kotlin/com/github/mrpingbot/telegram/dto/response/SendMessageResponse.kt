package com.github.mrpingbot.telegram.dto.response

import com.github.mrpingbot.telegram.dto.common.TelegramMessage

data class SendMessageResponse(
    val ok: Boolean,
    val result: TelegramMessage
)
