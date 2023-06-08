package com.github.mrpingbot.gitlab

import com.github.mrpingbot.common.PagedResult
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequest
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequestApprove
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequestComment
import com.github.mrpingbot.gitlab.dto.response.GitlabProject
import com.github.mrpingbot.vpn.VpnConnectorTemplate
import org.springframework.stereotype.Service

@Service
class GitlabService(
    private val gitlabClient: GitlabClient,
    private val vpnConnectorTemplate: VpnConnectorTemplate,
) {
    companion object {
        const val START_PAGE = 0
    }

    fun getMergeRequest(
        projectId: GitlabProjectId,
        mergeRequestIid: Long
    ): GitlabMergeRequest = vpnConnectorTemplate.execute {
        gitlabClient.getMergeRequest(
            projectId.build(),
            mergeRequestIid
        )
    }

    fun getProject(
        projectId: GitlabProjectId
    ): GitlabProject = vpnConnectorTemplate.execute {
        gitlabClient.getProject(projectId.build())
    }

    fun getMergeRequests(
        projectId: GitlabProjectId,
        mergeRequestIids: List<Long>
    ): List<GitlabMergeRequest> = vpnConnectorTemplate.execute {
        gitlabClient.getMergeRequests(
            projectId.build(),
            mergeRequestIids
        )
    }

    fun getMergeRequestsByStatus(
        projectId: GitlabProjectId,
        statutes: List<String>,
        page: Int,
        pageSize: Int
    ): PagedResult<GitlabMergeRequest> = vpnConnectorTemplate.execute {
        gitlabClient.getMergeRequestsByStatus(
            projectId.build(),
            statutes,
            page,
            pageSize
        )
    }

    fun getMergeRequestsComments(
        projectId: GitlabProjectId,
        mergeRequestIid: Long,
    ): List<GitlabMergeRequestComment> = vpnConnectorTemplate.execute {
        gitlabClient.getMergeRequestComments(
            projectId.build(),
            mergeRequestIid
        )
    }

    fun getMergeRequestApprovals(
        projectId: GitlabProjectId,
        mergeRequestIid: Long
    ): GitlabMergeRequestApprove = vpnConnectorTemplate.execute {
        gitlabClient.getMergeRequestApprovals(
            projectId.build(),
            mergeRequestIid
        )
    }
}

private fun GitlabProjectId.build(): String = "${this.namespace()}/${this.group()}/${this.name()}"
