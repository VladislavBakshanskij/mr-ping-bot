package com.github.mrpingbot.rewiever

import com.github.jooq.tables.references.MERGE_REQUESTS
import com.github.jooq.tables.references.MERGE_REQUEST_NOTIFICATIONS
import com.github.jooq.tables.references.REVIEWERS
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Repository

@Repository
internal class JooqReviewerRepository(private val dslContext: DSLContext) : ReviewerRepository {
    private val mapper: RecordMapper<Record, Reviewer> = RecordMapper {
        Reviewer(
            it.get(REVIEWERS.ID)!!,
            it.get(REVIEWERS.NICKNAME)!!,
            it.get(REVIEWERS.PERSONAL_CHAT_ID)
        )
    }

    override fun findAllByMergeRequestIds(ids: List<Long>): List<Reviewer> = dslContext.select()
        .from(REVIEWERS)
        .join(MERGE_REQUEST_NOTIFICATIONS).on(MERGE_REQUEST_NOTIFICATIONS.REVIEWER_ID.equal(REVIEWERS.ID))
        .join(MERGE_REQUESTS).on(MERGE_REQUEST_NOTIFICATIONS.MERGE_REQUEST_ID.equal(MERGE_REQUESTS.ID))
        .where(MERGE_REQUESTS.ID.`in`(ids))
        .fetch()
        .map(mapper)
        .toList()

    override fun save(reviewer: Reviewer): Reviewer {
        dslContext.insertInto(REVIEWERS)
            .set(REVIEWERS.ID, reviewer.id)
            .set(REVIEWERS.NICKNAME, reviewer.nickname)
            .set(REVIEWERS.PERSONAL_CHAT_ID, reviewer.personalChatId)
            .execute()

        return reviewer
    }

    override fun findById(
        id: Long
    ): Reviewer? = dslContext.selectFrom(REVIEWERS)
        .where(REVIEWERS.ID.equal(id))
        .fetchOne()
        ?.map(mapper)

    override fun update(reviewer: Reviewer) {
        dslContext.update(REVIEWERS)
            .set(REVIEWERS.PERSONAL_CHAT_ID, reviewer.personalChatId)
            .set(REVIEWERS.GITLAB_USERNAME, reviewer.gitlabUsername)
            .execute()
    }

    override fun findByGitlabUsername(gitlabUsername: String): Reviewer? = dslContext.selectFrom(REVIEWERS)
        .where(REVIEWERS.GITLAB_USERNAME.equal(gitlabUsername))
        .fetchOne()
        ?.map(mapper)
}
