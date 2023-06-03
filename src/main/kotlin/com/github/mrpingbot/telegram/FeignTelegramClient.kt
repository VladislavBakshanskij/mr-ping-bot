package com.github.mrpingbot.telegram

import com.github.mrpingbot.telegram.dto.request.SendMessageRequest
import com.github.mrpingbot.telegram.dto.response.SendMessageResponse
import feign.Logger.*
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    url = "\${telegram.url}",
    name = "telegram-client",
    configuration = [FeignTelegramClient.Config::class]
)
interface FeignTelegramClient : TelegramClient {
    class Config {
        @Bean
        fun loggerLevel(): Level = Level.FULL
    }

    @PostMapping("sendMessage")
    override fun sendMessage(@RequestBody request: SendMessageRequest): SendMessageResponse
}
