package com.github.mrpingbot.gitlab

interface GitlabProjectId {
    fun namespace(): String

    fun group(): String

    fun name(): String
}
