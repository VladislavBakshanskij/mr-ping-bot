package com.github.mrpingbot.mergeRequestNotifications

interface MergeRequestNotificationsRepository {
    fun findByMergeRequestIdAndReviewerId(mergeRequestId: Long, reviewerId: Long): MergeRequestNotifications?

    fun save(mergeRequestNotifications: MergeRequestNotifications)
}
