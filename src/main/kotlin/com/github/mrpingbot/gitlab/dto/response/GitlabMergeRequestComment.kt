package com.github.mrpingbot.gitlab.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class GitlabMergeRequestComment(
    val type: GitlabMergeRequestCommentType?,
    val system: Boolean = false,
    val author: GitlabUser,
    val resolvable: Boolean = false,
    val resolved: Boolean = false,
)

enum class GitlabMergeRequestCommentType {
    @field:JsonProperty("DiffNote")
    DIFF_NOTE,
    ;

//    companion object {
//        @JsonCreator
//        @JvmStatic
//        fun fromGitlabName(gitlabName: String?): GitlabMergeRequestCommentType? =
//            values().find { it.gitlabName == gitlabName }
//    }
}
