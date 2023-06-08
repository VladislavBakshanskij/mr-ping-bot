package com.github.mrpingbot.mergeRequestNotifications

import com.github.jooq.tables.records.MergeRequestNotificationsRecord
import com.github.jooq.tables.references.MERGE_REQUEST_NOTIFICATIONS
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
internal class JooqMergeRequestNotificationsRepository(
    private val dslContext: DSLContext
) : MergeRequestNotificationsRepository {
    private val mapper: (record: Record) -> MergeRequestNotifications? = {
        val mergeRequestNotificationsRecord = it as MergeRequestNotificationsRecord
        MergeRequestNotifications(
            mergeRequestNotificationsRecord.mergeRequestId,
            mergeRequestNotificationsRecord.reviewerId,
            mergeRequestNotificationsRecord.approve!!
        )
    }

    override fun findByMergeRequestIdAndReviewerId(
        mergeRequestId: Long, reviewerId: Long
    ): MergeRequestNotifications? = dslContext.selectFrom(MERGE_REQUEST_NOTIFICATIONS)
        .where(
            MERGE_REQUEST_NOTIFICATIONS.MERGE_REQUEST_ID.equal(mergeRequestId)
                .and(MERGE_REQUEST_NOTIFICATIONS.REVIEWER_ID.equal(reviewerId))
        )
        .fetchOne()
        ?.map(mapper)

    override fun save(mergeRequestNotifications: MergeRequestNotifications) {
        dslContext.insertInto(MERGE_REQUEST_NOTIFICATIONS)
            .set(MERGE_REQUEST_NOTIFICATIONS.MERGE_REQUEST_ID, mergeRequestNotifications.mergeRequestId)
            .set(MERGE_REQUEST_NOTIFICATIONS.REVIEWER_ID, mergeRequestNotifications.reviewerId)
            .set(MERGE_REQUEST_NOTIFICATIONS.APPROVE, mergeRequestNotifications.approve)
            .execute()
    }
}