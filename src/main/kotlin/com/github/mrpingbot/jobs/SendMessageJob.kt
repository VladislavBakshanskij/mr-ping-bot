package com.github.mrpingbot.jobs

import com.github.mrpingbot.mergeRequest.MergeRequestService
import com.github.mrpingbot.rewiever.ReviewerService
import com.github.mrpingbot.telegram.TelegramService
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
    private val mergeRequestService: MergeRequestService,
    private val reviewerService: ReviewerService,
    private val telegramService: TelegramService,
    @Value("\${job.send-message.period-in-minutes}") private val periodInMinutes: Long,
) {
    @Scheduled(cron = "\${job.send-message.cron}")
    fun handleOldMergeRequests() {
        val dateToSearch = Instant.now().minus(periodInMinutes, ChronoUnit.MINUTES)
        val mergeRequests =
            mergeRequestService.getAllByLastModifiedDateLessThan(dateToSearch)

        mergeRequests.groupBy { it.messageId }
            .forEach { (messageId, mergeRequests) ->
                val reviewers =
                    reviewerService.getReviewersByMergeRequestIds(mergeRequests.map { it.id })
                // МРы в рамках одного сообщения имеют один и тот чат
                val chatId = mergeRequests.map { it.chatId }.first()

                if (reviewers.isEmpty()) {
                    telegramService.replyMessage(
                        chatId,
                        messageId,
                        "ПОСМОТРИТЕ МР", // todo изменить текстовку
                    )
                }

                mergeRequests.map { it.updateLastModifiedDate(Instant.now()) }
                    .forEach { mergeRequestService.update(it) }
            }
    }
}
