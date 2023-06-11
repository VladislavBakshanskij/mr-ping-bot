package com.github.mrpingbot.config

import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import java.nio.charset.StandardCharsets

@Configuration
class MessageSourceConfig {
    @Bean
    fun messageSource(): MessageSource {
        val resourceBundleMessageSource = ResourceBundleMessageSource()
        resourceBundleMessageSource.setBasename("i18n/messages")
        resourceBundleMessageSource.setDefaultEncoding(StandardCharsets.UTF_8.name())
        return resourceBundleMessageSource
    }
}
