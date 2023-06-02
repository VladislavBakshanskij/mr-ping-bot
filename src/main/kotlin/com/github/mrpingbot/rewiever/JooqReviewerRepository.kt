package com.github.mrpingbot.rewiever

import com.github.jooq.tables.references.MERGE_REQUESTS
import com.github.jooq.tables.references.MERGE_REQUEST_NOTIFICATIONS
import com.github.jooq.tables.references.REVIEWERS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
internal class JooqReviewerRepository(private val dslContext: DSLContext) : ReviewerRepository {
    override fun findAllByMergeRequestIds(ids: List<Long>): List<Reviewer> = dslContext.select()
        .from(REVIEWERS)
        .join(MERGE_REQUEST_NOTIFICATIONS).on(MERGE_REQUEST_NOTIFICATIONS.REVIEWER_ID.equal(REVIEWERS.ID))
        .join(MERGE_REQUESTS).on(MERGE_REQUEST_NOTIFICATIONS.MERGE_REQUEST_ID.equal(MERGE_REQUESTS.ID))
        .where(MERGE_REQUESTS.ID.`in`(ids))
        .fetch()
        .map {
            Reviewer(
                it.get(REVIEWERS.ID)!!,
                it.get(REVIEWERS.NICKNAME)!!,
                it.get(REVIEWERS.PERSONAL_CHAT_ID)!!
            )
        }
        .toList()

}
