package com.github.mrpingbot.utils

import org.springframework.context.MessageSource
import java.util.*

fun MessageSource.getMessage(code: String): String = this.getMessage(code, emptyArray(), Locale.ROOT)
