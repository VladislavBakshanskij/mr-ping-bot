package com.github.mrpingbot.rewiever

interface ReviewerRepository {
    fun findAllByMergeRequestIds(ids: List<Long>): List<Reviewer>

    fun save(reviewer: Reviewer): Reviewer

    fun findById(id: Long): Reviewer?

    fun update(reviewer: Reviewer)

    fun findByGitlabUsername(gitlabUsername: String): Reviewer?
}