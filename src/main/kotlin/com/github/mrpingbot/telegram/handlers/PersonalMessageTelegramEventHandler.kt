package com.github.mrpingbot.telegram.handlers

import com.github.mrpingbot.gitlab.GitlabService
import com.github.mrpingbot.mergeRequest.MergeRequestService
import com.github.mrpingbot.message.Message
import com.github.mrpingbot.message.MessageService
import com.github.mrpingbot.project.ProjectService
import com.github.mrpingbot.rewiever.ReviewerService
import com.github.mrpingbot.telegram.TelegramService
import com.github.mrpingbot.telegram.dto.request.UpdateRequest
import com.github.mrpingbot.utils.replaceAllNotLetter
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class PersonalMessageTelegramEventHandler(
    gitlabService: GitlabService,
    private val messageService: MessageService,
    private val projectService: ProjectService,
    reviewerService: ReviewerService,
    private val mergeRequestService: MergeRequestService,
    @Value("\${telegram.code-review-chat-id:-835343898}") private val chatId: Long,
    private val telegramService: TelegramService,
) : AbstractTelegramEventHandler(
    gitlabService,
    messageService,
    projectService,
    reviewerService,
    mergeRequestService,
    chatId
), TelegramEventHandler {

    companion object {
        private const val MR_DELIMITER = "\n\n--------------\n"
        private const val MR_HASH_TAG = "#mr "
    }

    override fun supportEvent(): TelegramEvent = TelegramEvent.PERSONAL_MESSAGE

    override fun handle(updateRequest: UpdateRequest) {
        val createdMergeRequests = createMergeRequests(updateRequest)

        val stringJoiner = StringJoiner(MR_DELIMITER, MR_HASH_TAG, StringUtils.EMPTY)
        for (mergeRequest in createdMergeRequests) {
            val project = projectService.getById(mergeRequest.projectId)
            stringJoiner.add(
                """
                    #${project.name.replaceAllNotLetter("_")}

                    ${mergeRequest.link}
                """.trimIndent()
            )
        }

        val sendMessageResult = telegramService.sendMessage(
            chatId,
            stringJoiner.toString()
        )
        val savedMessage = messageService.createIfNotExists(
            Message(
                sendMessageResult.messageId,
                chatId,
                sendMessageResult.text,
            )
        )

        // для корректной работы reply необходимо обновить messageId
        createdMergeRequests.map { it.updateMessageId(savedMessage.id) }
            .forEach { mergeRequestService.update(it) }
    }
}
