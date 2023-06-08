package com.github.mrpingbot.gitlab

import com.github.mrpingbot.common.PagedResult
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequest
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequestApprove
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequestComment
import com.github.mrpingbot.gitlab.dto.response.GitlabProject

interface GitlabClient {
    fun getMergeRequest(projectId: String, mergeRequestIid: Long): GitlabMergeRequest

    fun getProject(projectId: String): GitlabProject

    fun getMergeRequests(projectId: String, iids: List<Long>): List<GitlabMergeRequest>

    fun getMergeRequestComments(projectId: String, mergeRequestIid: Long): List<GitlabMergeRequestComment>

    fun getMergeRequestsByStatus(
        projectId: String,
        statutes: List<String>,
        page: Int,
        pageSize: Int
    ): PagedResult<GitlabMergeRequest>

    fun getMergeRequestApprovals(projectId: String, mergeRequestIid: Long): GitlabMergeRequestApprove
}
