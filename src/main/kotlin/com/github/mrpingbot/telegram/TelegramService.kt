package com.github.mrpingbot.telegram

import com.github.mrpingbot.telegram.dto.common.TelegramMessage
import com.github.mrpingbot.telegram.dto.request.SendMessageRequest
import com.github.mrpingbot.vpn.VpnConnectorTemplate
import org.springframework.stereotype.Service

@Service
class TelegramService(
    private val telegramClient: TelegramClient,
    private val vpnConnectorTemplate: VpnConnectorTemplate
) {
    fun sendMessage(
        chatId: Long,
        message: String
    ): TelegramMessage = vpnConnectorTemplate.execute {
        telegramClient.sendMessage(
            SendMessageRequest(
                chatId,
                message
            )
        ).result
    }

    fun replyMessage(
        chatId: Long,
        messageId: Long,
        message: String
    ) = vpnConnectorTemplate.execute {
        telegramClient.sendMessage(
            SendMessageRequest(
                chatId,
                message,
                messageId
            )
        ).result
    }
}
