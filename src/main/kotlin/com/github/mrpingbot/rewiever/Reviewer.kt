package com.github.mrpingbot.rewiever

data class Reviewer(
    val id: Long,
    val nickname: String? = null,
    val personalChatId: Long? = null,
    val gitlabUsername: String? = null
) {
    fun updatePersonalChatAndGitlabUsername(chatId: Long, gitlabUsername: String): Reviewer =
        Reviewer(id, nickname, chatId, gitlabUsername)
}
