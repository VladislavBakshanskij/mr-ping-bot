package com.github.mrpingbot.gitlab.dto.response

data class GitlabProject(
    val id: Long,
    val path: String,
    val name: String,
    val namespace: GitlabProjectNamespace,
) {
    fun getNamespaceName(): String = path.split("/").first()

    fun getGroupName(): String = namespace.name
}

data class GitlabProjectNamespace(
    val id: Long,
    val name: String,
)
