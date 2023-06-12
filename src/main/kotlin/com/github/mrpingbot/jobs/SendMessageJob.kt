package com.github.mrpingbot.jobs

import com.github.mrpingbot.mergeRequest.MergeRequestService
import com.github.mrpingbot.message.Message
import com.github.mrpingbot.message.MessageService
import com.github.mrpingbot.rewiever.ReviewerService
import com.github.mrpingbot.telegram.TelegramService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@ConditionalOnProperty(
    value = ["job.enabled", "job.send-message.enabled"],
    matchIfMissing = true,
)
@Component
class SendMessageJob(
    private val messageService: MessageService,
    private val reviewerService: ReviewerService,
    private val telegramService: TelegramService,
    private val mergeRequestService: MergeRequestService,
    @Value("\${job.send-message.period-in-minutes}") private val periodInMinutes: Long,
) {
    @Scheduled(cron = "\${job.send-message.cron}")
    fun replyNotifications() {
        val dateToSearch = Instant.now().minus(periodInMinutes, ChronoUnit.MINUTES)
        val mergeRequests =
            mergeRequestService.getByApproveAndLastModifiedDateLessThan(approve = false, dateToSearch)

        val messagesWithoutMergeRequests = messageService.getAllWithoutMergeRequests()
        messagesWithoutMergeRequests.forEach { telegramService.deleteMessage(it.id, it.chatId) }
        messageService.deleteAll(messagesWithoutMergeRequests)

        mergeRequests.groupBy { it.messageId }
            .forEach { (messageId, mergeRequests) ->
                val reviewers =
                    reviewerService.getReviewersByMergeRequestIds(mergeRequests.map { it.id })
                        .filter { it.nickname != null }
                // МРы в рамках одного сообщения имеют один и тот чат
                val message = messageService.getById(messageId)

                val telegramMessage = if (reviewers.isEmpty()) {
                    "ПОСМОТРИТЕ МР" // todo изменить текстовку
                } else {
                    reviewers.mapNotNull { it.nickname }
                        .joinToString(StringUtils.SPACE, "Просьба посмотреть МР\n\n") { "@$it" }
                }

                val replyMessageResponse = telegramService.replyMessage(
                    message.chatId,
                    message.id,
                    telegramMessage
                )

                mergeRequests.map { it.updateLastModifiedDate(Instant.now()) }
                    .forEach { mergeRequestService.update(it) }

                messageService.createIfNotExists(
                    Message(
                        replyMessageResponse.messageId,
                        replyMessageResponse.chat.id,
                    )
                )
            }

        // отправляем МРы, которые готовы для заливки
        mergeRequestService.getByApproveAndLastModifiedDateLessThan(approve = true, dateToSearch)
            .groupBy { it.authorId }
            .mapKeys { reviewerService.findById(it.key)!! }
            .filterKeys { it.personalChatId != null }
            .forEach { (author, mergeRequests) ->
                telegramService.sendMessage(
                    author.personalChatId!!,
                    """
                       Следующие МРы готоы для деплоя:
                       
                       ${mergeRequests.joinToString("\n") { it.link }}
                    """.trimIndent()
                )
                mergeRequestService.deleteAll(mergeRequests)
            }
    }
}
