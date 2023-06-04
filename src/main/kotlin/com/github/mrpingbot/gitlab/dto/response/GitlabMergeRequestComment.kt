package com.github.mrpingbot.gitlab.dto.response

import com.fasterxml.jackson.annotation.JsonCreator

data class GitlabMergeRequestComment(
    val type: GitlabMergeRequestCommentType?,
    val system: Boolean = false,
    val author: GitlabAuthor,
    val resolvable: Boolean = false,
    val resolved: Boolean = false,
)

enum class GitlabMergeRequestCommentType(val gitlabName: String) {
    DIFF_NOTE("DiffNote"),
    ;

    companion object {
        @JsonCreator
        fun fromGitlabName(gitlabName: String): GitlabMergeRequestCommentType? =
            values().find { it.gitlabName == gitlabName }
    }
}
