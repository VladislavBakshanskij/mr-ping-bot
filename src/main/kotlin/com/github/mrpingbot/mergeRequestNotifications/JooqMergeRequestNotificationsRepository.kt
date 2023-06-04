package com.github.mrpingbot.mergeRequestNotifications

import com.github.jooq.tables.records.MergeRequestNotificationsRecord
import com.github.jooq.tables.references.MERGE_REQUEST_NOTIFICATIONS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqMergeRequestNotificationsRepository(
    private val dslContext: DSLContext
) : MergeRequestNotificationsRepository {
    override fun findByMergeRequestIdAndReviewerId(
        mergeRequestId: Long, reviewerId: Long
    ): MergeRequestNotifications? = dslContext.selectFrom(MERGE_REQUEST_NOTIFICATIONS)
        .where(
            MERGE_REQUEST_NOTIFICATIONS.MERGE_REQUEST_ID.equal(mergeRequestId)
                .and(MERGE_REQUEST_NOTIFICATIONS.REVIEWER_ID.equal(reviewerId))
        )
        .fetchOne()
        ?.map {
            val mergeRequestNotificationsRecord = it as MergeRequestNotificationsRecord
            MergeRequestNotifications(
                mergeRequestNotificationsRecord.mergeRequestId,
                mergeRequestNotificationsRecord.reviewerId,
            )
        }

    override fun save(mergeRequestNotifications: MergeRequestNotifications) {
        dslContext.insertInto(MERGE_REQUEST_NOTIFICATIONS)
            .set(MERGE_REQUEST_NOTIFICATIONS.MERGE_REQUEST_ID, mergeRequestNotifications.mergeRequestId)
            .set(MERGE_REQUEST_NOTIFICATIONS.REVIEWER_ID, mergeRequestNotifications.reviewerId)
            .execute()
    }
}