package com.github.mrpingbot.message

import com.github.jooq.tables.records.MessagesRecord
import com.github.jooq.tables.references.MESSAGES
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqMessageRepository(
    private val dslContext: DSLContext
) : MessageRepository {
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
        ?.map {
            val messagesRecord = it as MessagesRecord
            Message(
                messagesRecord.id,
                messagesRecord.chatId,
                messagesRecord.text
            )
        }
}
