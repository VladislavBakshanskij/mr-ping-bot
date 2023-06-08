package com.github.mrpingbot.gitlab.dto.response

data class GitlabMergeRequestApprove(
    val approvedBy: List<GitlabMergeRequestApproveBy>
)

data class GitlabMergeRequestApproveBy(
    val user: GitlabUser
)
