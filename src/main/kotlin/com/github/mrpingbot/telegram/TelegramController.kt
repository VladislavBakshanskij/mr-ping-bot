package com.github.mrpingbot.telegram

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mrpingbot.telegram.dto.request.UpdateRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("telegram")
class TelegramController(
    private val telegramUpdateAsyncFacade: TelegramUpdateAsyncFacade,
    private val objectMapper: ObjectMapper,
) {
    @PostMapping("update")
    fun handleUpdate(@RequestBody request: UpdateRequest) {
//        println(objectMapper.writeValueAsString(request))
        telegramUpdateAsyncFacade.handleUpdate(request)
    }
}