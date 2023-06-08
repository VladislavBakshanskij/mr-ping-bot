package com.github.mrpingbot.message

import com.github.jooq.tables.references.MERGE_REQUESTS
import com.github.jooq.tables.references.MESSAGES
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
internal class JooqMessageRepository(
    private val dslContext: DSLContext
) : MessageRepository {
    private val mapper: (Record) -> Message = {
        Message(
            it.get(MESSAGES.ID)!!,
            it.get(MESSAGES.CHAT_ID)!!,
            it.get(MESSAGES.TEXT)
        )
    }

    override fun save(message: Message): Message {
        dslContext.insertInto(MESSAGES)
            .set(MESSAGES.ID, message.id)
            .set(MESSAGES.CHAT_ID, message.chatId)
            .set(MESSAGES.TEXT, message.text)
            .execute()

        return message
    }

    override fun findById(id: Long): Message? = dslContext.selectFrom(MESSAGES)
        .where(MESSAGES.ID.equal(id))
        .fetchOne()
        ?.map(mapper)

    override fun findAllWithoutMergeRequests(): List<Message> = dslContext.select()
        .from(MESSAGES)
        .leftAntiJoin(MERGE_REQUESTS).on(MERGE_REQUESTS.MESSAGE_ID.equal(MESSAGES.ID))
        .fetch()
        .map(mapper)

    override fun deleteAll(messages: List<Message>) {
        dslContext.deleteFrom(MESSAGES)
            .where(MESSAGES.ID.`in`(messages.map { it.id }))
            .execute()
    }
}
