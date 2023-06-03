package com.github.mrpingbot.utils

private val NOT_LETTER = Regex("[^a-zA-Z0-9]")

fun String.replaceAllNotLetter(newValue: String): String = this.replace(NOT_LETTER, newValue)

fun String.notContains(search: String) = !this.contains(search)
