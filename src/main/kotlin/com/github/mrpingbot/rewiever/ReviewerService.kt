package com.github.mrpingbot.rewiever

import org.springframework.stereotype.Service

@Service
class ReviewerService(private val reviewerRepository: ReviewerRepository) {
    fun getReviewersByMergeRequestIds(ids: List<Long>): List<Reviewer> =
        reviewerRepository.findAllByMergeRequestIds(ids)
}