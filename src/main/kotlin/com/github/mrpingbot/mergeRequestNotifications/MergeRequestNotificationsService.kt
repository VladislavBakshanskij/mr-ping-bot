package com.github.mrpingbot.mergeRequestNotifications

import org.springframework.stereotype.Service

@Service
class MergeRequestNotificationsService(
    private val mergeRequestNotificationsRepository: MergeRequestNotificationsRepository
) {
    fun createIfNotExists(mergeRequestNotifications: MergeRequestNotifications) {
        val foundedReviewerNotifications = mergeRequestNotificationsRepository.findByMergeRequestIdAndReviewerId(
            mergeRequestNotifications.mergeRequestId,
            mergeRequestNotifications.reviewerId
        )

        if (foundedReviewerNotifications == null) {
            mergeRequestNotificationsRepository.save(mergeRequestNotifications)
        }
    }
}