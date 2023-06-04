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

    fun update(reviewer: Reviewer) = reviewerRepository.update(reviewer)

    fun findByGitlabUsername(gitlabUsername: String): Reviewer? =
        reviewerRepository.findByGitlabUsername(gitlabUsername)

    fun findById(id: Long): Reviewer? = reviewerRepository.findById(id)

    fun existsByGitlabUsername(gitlabUsername: String): Boolean =
        findByGitlabUsername(gitlabUsername) != null
}