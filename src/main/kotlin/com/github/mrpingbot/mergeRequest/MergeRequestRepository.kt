package com.github.mrpingbot.mergeRequest

import java.time.Instant

interface MergeRequestRepository {
    fun save(mergeRequest: MergeRequest): MergeRequest

    fun findById(id: Long): MergeRequest?

    fun findByApproveAndLastModifiedDateLessThan(approve: Boolean, date: Instant): List<MergeRequest>

    fun update(mergeRequest: MergeRequest)

    fun findAllByStatuses(statuses: List<String>): List<MergeRequest>
}