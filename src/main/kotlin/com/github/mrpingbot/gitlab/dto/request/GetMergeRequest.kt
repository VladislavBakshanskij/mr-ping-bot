package com.github.mrpingbot.gitlab.dto.request

data class GetMergeRequest(
    val namespace: String,
    val group: String,
    val projectName: String,
    val mergeRequestIid: Long,
)
