package com.github.mrpingbot.gitlab

data class SimpleGitlabProjectId(
    val namespace: String,
    val group: String,
    val name: String,
) : GitlabProjectId {
    override fun namespace(): String = namespace

    override fun group(): String = group

    override fun name(): String = name
}