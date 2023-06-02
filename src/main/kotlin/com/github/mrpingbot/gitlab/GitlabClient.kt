package com.github.mrpingbot.gitlab

import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequest
import com.github.mrpingbot.gitlab.dto.response.GitlabProject

interface GitlabClient {
    fun getMergeRequest(projectNameWithNamespace: String, mergeRequestIid: Long): GitlabMergeRequest

    fun getProject(projectNameWithNamespace: String): GitlabProject
}
