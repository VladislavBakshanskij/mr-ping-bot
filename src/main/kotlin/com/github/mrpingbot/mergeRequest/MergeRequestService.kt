package com.github.mrpingbot.mergeRequest

import org.springframework.stereotype.Service
import java.time.Instant

@Service
class MergeRequestService(private val mergeRequestRepository: MergeRequestRepository) {
    fun createIfNotExists(mergeRequest: MergeRequest): MergeRequest {
        val foundedMergeRequest = mergeRequestRepository.findById(mergeRequest.id)
        return foundedMergeRequest ?: mergeRequestRepository.save(mergeRequest)
    }

    fun getAllByLastModifiedDateLessThan(date: Instant): List<MergeRequest> =
        mergeRequestRepository.findAllByLastModifiedDateLessThan(date)

    fun update(
        mergeRequest: MergeRequest
    ) = mergeRequestRepository.update(mergeRequest)
}
