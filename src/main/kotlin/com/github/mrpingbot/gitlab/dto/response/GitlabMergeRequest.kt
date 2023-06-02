package com.github.mrpingbot.gitlab.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class GitlabMergeRequest(
    val id: Long,
    val iid: Long,
    val projectId: Long,
    val draft: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    @field:JsonProperty("state") val status: String
)
