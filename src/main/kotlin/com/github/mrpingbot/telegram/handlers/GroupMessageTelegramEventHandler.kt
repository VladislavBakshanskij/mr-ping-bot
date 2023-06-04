package com.github.mrpingbot.telegram.handlers

import com.github.mrpingbot.gitlab.GitlabService
import com.github.mrpingbot.mergeRequest.MergeRequestService
import com.github.mrpingbot.message.MessageService
import com.github.mrpingbot.project.ProjectService
import com.github.mrpingbot.rewiever.ReviewerService
import com.github.mrpingbot.telegram.dto.request.UpdateRequest
import org.springframework.stereotype.Component

@Component
class GroupMessageTelegramEventHandler(
    gitlabService: GitlabService,
    messageService: MessageService,
    projectService: ProjectService,
    reviewerService: ReviewerService,
    mergeRequestService: MergeRequestService,
) : AbstractTelegramEventHandler(
    gitlabService,
    messageService,
    projectService,
    reviewerService,
    mergeRequestService
), TelegramEventHandler {
    override fun supportEvent(): TelegramEvent = TelegramEvent.GROUP_MESSAGE

    override fun handle(updateRequest: UpdateRequest) {
        createMergeRequests(updateRequest)
    }
}
