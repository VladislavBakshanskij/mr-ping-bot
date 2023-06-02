package com.github.mrpingbot.mergeRequest

import java.time.Instant

interface MergeRequestRepository {
    fun save(mergeRequest: MergeRequest): MergeRequest

    fun findById(id: Long): MergeRequest?

    fun findAllByLastModifiedDateLessThan(date: Instant): List<MergeRequest>

    fun update(mergeRequest: MergeRequest)
}