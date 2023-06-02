package com.github.mrpingbot.rewiever

interface ReviewerRepository {
    fun findAllByMergeRequestIds(ids: List<Long>): List<Reviewer>
}