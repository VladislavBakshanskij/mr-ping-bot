package com.github.mrpingbot.telegram

import com.github.mrpingbot.telegram.dto.request.SendMessageRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(url = "\${telegram.url}", name = "telegram-client")
interface FeignTelegramClient : TelegramClient {
    @PostMapping("sendMessage")
    override fun sendMessage(@RequestBody request: SendMessageRequest)
}
