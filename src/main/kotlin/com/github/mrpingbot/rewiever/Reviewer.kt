package com.github.mrpingbot.rewiever

data class Reviewer(
    val id: Long,
    val nickname: String? = null,
    val personalChatId: Long? = null
)
