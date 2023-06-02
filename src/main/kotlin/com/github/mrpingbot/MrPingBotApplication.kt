package com.github.mrpingbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MrPingBotApplication

fun main(args: Array<String>) {
    runApplication<MrPingBotApplication>(*args)
}
