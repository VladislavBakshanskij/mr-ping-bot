package com.github.mrpingbot.mergeRequest

import com.github.jooq.tables.references.MERGE_REQUESTS
import com.github.jooq.tables.references.MERGE_REQUEST_NOTIFICATIONS
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
internal class JooqMergeRequestRepository(private val dslContext: DSLContext) : MergeRequestRepository {
    private val mapper: (Record) -> MergeRequest = {
        MergeRequest(
            it.get(MERGE_REQUESTS.ID)!!,
            it.get(MERGE_REQUESTS.IID)!!,
            it.get(MERGE_REQUESTS.MESSAGE_ID)!!,
            it.get(MERGE_REQUESTS.PROJECT_ID)!!,
            it.get(MERGE_REQUESTS.LINK)!!,
            it.get(MERGE_REQUESTS.WIP)!!,
            it.get(MERGE_REQUESTS.CREATE_DATETIME)?.toInstant(ZoneOffset.UTC),
            it.get(MERGE_REQUESTS.STATUS)!!,
            it.get(MERGE_REQUESTS.LAST_MODIFY_DATETIME)?.toInstant(ZoneOffset.UTC),
        )
    }

    override fun save(mergeRequest: MergeRequest): MergeRequest {
        dslContext.insertInto(MERGE_REQUESTS)
            .set(MERGE_REQUESTS.ID, mergeRequest.id)
            .set(MERGE_REQUESTS.IID, mergeRequest.iid)
            .set(MERGE_REQUESTS.MESSAGE_ID, mergeRequest.messageId)
            .set(MERGE_REQUESTS.PROJECT_ID, mergeRequest.projectId)
            .set(MERGE_REQUESTS.LINK, mergeRequest.link)
            .set(MERGE_REQUESTS.WIP, mergeRequest.wip)
            .set(MERGE_REQUESTS.STATUS, mergeRequest.status)
            .set(
                MERGE_REQUESTS.CREATE_DATETIME,
                LocalDateTime.ofInstant(mergeRequest.createDatetime, ZoneOffset.systemDefault())
            )
            .set(
                MERGE_REQUESTS.LAST_MODIFY_DATETIME,
                LocalDateTime.ofInstant(mergeRequest.lastModifiedDatetime, ZoneOffset.UTC)
            )
            .execute()
        return mergeRequest
    }

    override fun findById(id: Long): MergeRequest? = dslContext.selectFrom(MERGE_REQUESTS)
        .where(MERGE_REQUESTS.ID.equal(id))
        .fetchOne()
        ?.map(mapper)

    override fun findByApproveAndLastModifiedDateLessThan(approve: Boolean, date: Instant): List<MergeRequest> {
        val approveCondition = MERGE_REQUEST_NOTIFICATIONS.APPROVE.equal(approve)
        return dslContext.selectDistinct()
            .from(MERGE_REQUESTS)
            .leftJoin(MERGE_REQUEST_NOTIFICATIONS)
            .on(MERGE_REQUEST_NOTIFICATIONS.MERGE_REQUEST_ID.equal(MERGE_REQUESTS.ID))
            .where(
                MERGE_REQUESTS.LAST_MODIFY_DATETIME.lessOrEqual(
                    date.atZone(ZoneOffset.systemDefault()).toLocalDateTime()
                )
            ).and(
                if (approve) {
                    MERGE_REQUEST_NOTIFICATIONS.MERGE_REQUEST_ID.isNull.or(approveCondition)
                } else {
                    approveCondition
                }
            )
            .fetch()
            .map(mapper)
            .toList()
    }

    override fun update(mergeRequest: MergeRequest) {
        dslContext.update(MERGE_REQUESTS)
            .set(MERGE_REQUESTS.MESSAGE_ID, mergeRequest.messageId)
            .set(
                MERGE_REQUESTS.LAST_MODIFY_DATETIME,
                LocalDateTime.ofInstant(mergeRequest.lastModifiedDatetime, ZoneOffset.UTC)
            )
            .where(MERGE_REQUESTS.ID.equal(mergeRequest.id))
            .execute()
    }

    override fun findAllByStatuses(
        statuses: List<String>
    ): List<MergeRequest> = dslContext.selectFrom(MERGE_REQUESTS)
        .where(MERGE_REQUESTS.STATUS.`in`(statuses))
        .fetch()
        .map(mapper)
        .toList()
}
