package com.github.mrpingbot.telegram.handlers

import com.github.mrpingbot.gitlab.GitlabService
import com.github.mrpingbot.mergeRequest.MergeRequestService
import com.github.mrpingbot.message.MessageService
import com.github.mrpingbot.project.ProjectService
import com.github.mrpingbot.rewiever.ReviewerService
import com.github.mrpingbot.telegram.dto.request.UpdateRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class GroupMessageTelegramEventHandler(
    gitlabService: GitlabService,
    messageService: MessageService,
    projectService: ProjectService,
    reviewerService: ReviewerService,
    mergeRequestService: MergeRequestService,
    @Value("\${telegram.code-review-chat-id:-835343898}") private val chatId: Long,
) : AbstractTelegramEventHandler(
    gitlabService,
    messageService,
    projectService,
    reviewerService,
    mergeRequestService,
    chatId
), TelegramEventHandler {
    override fun supportEvent(): TelegramEvent = TelegramEvent.GROUP_MESSAGE

    override fun handle(updateRequest: UpdateRequest) {
        createMergeRequests(updateRequest)
    }
}
