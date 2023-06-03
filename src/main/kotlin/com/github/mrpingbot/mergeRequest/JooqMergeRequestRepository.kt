package com.github.mrpingbot.mergeRequest

import com.github.jooq.tables.records.MergeRequestsRecord
import com.github.jooq.tables.references.MERGE_REQUESTS
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.RecordMapper
import org.springframework.stereotype.Repository
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Repository
internal class JooqMergeRequestRepository(private val dslContext: DSLContext) : MergeRequestRepository {
    private val mapper: RecordMapper<Record, MergeRequest> = RecordMapper {
        val mergeRequestsRecord = it as MergeRequestsRecord
        MergeRequest(
            mergeRequestsRecord.id,
            mergeRequestsRecord.iid,
            mergeRequestsRecord.messageId,
            mergeRequestsRecord.projectId,
            mergeRequestsRecord.link,
            mergeRequestsRecord.wip!!,
            mergeRequestsRecord.createDatetime?.toInstant(ZoneOffset.UTC),
            mergeRequestsRecord.status,
            mergeRequestsRecord.lastModifyDatetime?.toInstant(ZoneOffset.UTC),
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

    override fun findAllByLastModifiedDateLessThan(date: Instant): List<MergeRequest> =
        dslContext.selectFrom(MERGE_REQUESTS)
            .where(
                MERGE_REQUESTS.LAST_MODIFY_DATETIME.lessOrEqual(
                    date.atZone(ZoneOffset.systemDefault()).toLocalDateTime()
                )
            )
            .fetch()
            .map(mapper)
            .toList()

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

}
