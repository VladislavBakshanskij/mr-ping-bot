package com.github.mrpingbot.mergeRequest

import java.time.Instant

data class MergeRequest(
    val id: Long,
    val iid: Long,
    val messageId: Long,
    val projectId: Long,
    val link: String,
    val wip: Boolean,
    val createDatetime: Instant?,
    val status: String,
    val lastModifiedDatetime: Instant?,
    val authorId: Long,
) {
    fun updateLastModifiedDate(
        lastModifiedDatetime: Instant
    ): MergeRequest = MergeRequest(
        id,
        iid,
        messageId,
        projectId,
        link,
        wip,
        createDatetime,
        status,
        lastModifiedDatetime,
        authorId
    )

    fun updateMessageId(
        messageId: Long
    ): MergeRequest = MergeRequest(
        id,
        iid,
        messageId,
        projectId,
        link,
        wip,
        createDatetime,
        status,
        Instant.now(),
        authorId
    )

    fun updateStatus(
        status: String
    ): MergeRequest = MergeRequest(
        id,
        iid,
        messageId,
        projectId,
        link,
        wip,
        createDatetime,
        status,
        Instant.now(),
        authorId
    )
}
