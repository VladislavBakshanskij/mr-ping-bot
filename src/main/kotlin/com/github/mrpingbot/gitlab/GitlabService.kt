package com.github.mrpingbot.gitlab

import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequest
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequestComment
import com.github.mrpingbot.gitlab.dto.response.GitlabProject
import com.github.mrpingbot.vpn.VpnConnectorTemplate
import org.springframework.stereotype.Service

@Service
class GitlabService(
    private val gitlabClient: GitlabClient,
    private val vpnConnectorTemplate: VpnConnectorTemplate,
) {
    fun getMergeRequest(
        projectId: GitlabProjectId,
        mergeRequestIid: Long
    ): GitlabMergeRequest = vpnConnectorTemplate.execute {
        gitlabClient.getMergeRequest(
            buildProjectId(projectId),
            mergeRequestIid
        )
    }

    fun getProject(
        projectId: GitlabProjectId
    ): GitlabProject = vpnConnectorTemplate.execute {
        gitlabClient.getProject(buildProjectId(projectId))
    }

    fun getMergeRequests(
        projectId: GitlabProjectId,
        mergeRequestIids: List<Long>
    ): List<GitlabMergeRequest> = vpnConnectorTemplate.execute {
        gitlabClient.getMergeRequests(
            buildProjectId(projectId),
            mergeRequestIids
        )
    }

    private fun buildProjectId(projectId: GitlabProjectId) =
        "${projectId.namespace()}/${projectId.group()}/${projectId.name()}"
}
