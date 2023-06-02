package com.github.mrpingbot.gitlab.dto.request

data class GetProjectRequest(
    val namespace: String,
    val group: String,
    val projectName: String,
)
