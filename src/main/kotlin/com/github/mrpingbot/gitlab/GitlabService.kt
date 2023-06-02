package com.github.mrpingbot.gitlab

import com.github.mrpingbot.gitlab.dto.request.GetMergeRequest
import com.github.mrpingbot.gitlab.dto.request.GetProjectRequest
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequest
import com.github.mrpingbot.gitlab.dto.response.GitlabProject
import org.springframework.stereotype.Service

@Service
class GitlabService(private val gitlabClient: GitlabClient) {
    fun getMergeRequest(
        request: GetMergeRequest
    ): GitlabMergeRequest = gitlabClient.getMergeRequest(
        "${request.namespace}/${request.group}/${request.projectName}",
        request.mergeRequestIid
    )

    fun getProject(request: GetProjectRequest): GitlabProject =
        gitlabClient.getProject("${request.namespace}/${request.group}/${request.projectName}")
}
