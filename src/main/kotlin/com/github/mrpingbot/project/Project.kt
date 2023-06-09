package com.github.mrpingbot.project

import com.github.mrpingbot.gitlab.GitlabProjectId

data class Project(
    val id: Long,
    val name: String,
    val group: String,
    val namespace: String,
) : GitlabProjectId {
    override fun namespace(): String = namespace

    override fun group(): String = group

    override fun name(): String = name
}
