package com.github.mrpingbot.rewiever

import org.springframework.stereotype.Service

@Service
class ReviewerService(private val reviewerRepository: ReviewerRepository) {
    fun getReviewersByMergeRequestIds(ids: List<Long>): List<Reviewer> =
        reviewerRepository.findAllByMergeRequestIds(ids)

    fun createIfNotExists(reviewer: Reviewer): Reviewer {
        val foundedReviewer = reviewerRepository.findById(reviewer.id)
        return foundedReviewer ?: reviewerRepository.save(reviewer)
    }
}