package com.github.mrpingbot.mergeRequest

import org.springframework.stereotype.Service
import java.time.Instant

@Service
class MergeRequestService(private val mergeRequestRepository: MergeRequestRepository) {
    fun createIfNotExists(mergeRequest: MergeRequest): MergeRequest {
        val foundedMergeRequest = mergeRequestRepository.findById(mergeRequest.id)
        return foundedMergeRequest ?: mergeRequestRepository.save(mergeRequest)
    }

    fun getByApproveAndLastModifiedDateLessThan(approve: Boolean, date: Instant): List<MergeRequest> =
        mergeRequestRepository.findByApproveAndLastModifiedDateLessThan(approve, date)

    fun update(
        mergeRequest: MergeRequest
    ) = mergeRequestRepository.update(mergeRequest)

    fun getAllByStatuses(
        statuses: List<String>
    ): List<MergeRequest> = mergeRequestRepository.findAllByStatuses(statuses)

    fun existsById(id: Long): Boolean = mergeRequestRepository.findById(id) != null
}
