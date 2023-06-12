package com.github.mrpingbot.telegram.handlers

import com.github.mrpingbot.gitlab.GitlabProjectId
import com.github.mrpingbot.gitlab.GitlabService
import com.github.mrpingbot.gitlab.SimpleGitlabProjectId
import com.github.mrpingbot.mergeRequest.MergeRequest
import com.github.mrpingbot.mergeRequest.MergeRequestService
import com.github.mrpingbot.message.Message
import com.github.mrpingbot.message.MessageService
import com.github.mrpingbot.project.Project
import com.github.mrpingbot.project.ProjectService
import com.github.mrpingbot.rewiever.Reviewer
import com.github.mrpingbot.rewiever.ReviewerService
import com.github.mrpingbot.telegram.dto.common.TelegramMessage
import com.github.mrpingbot.telegram.dto.request.UpdateRequest
import java.time.Instant

abstract class AbstractTelegramEventHandler(
    private val gitlabService: GitlabService,
    private val messageService: MessageService,
    private val projectService: ProjectService,
    private val reviewerService: ReviewerService,
    private val mergeRequestService: MergeRequestService,
) : TelegramEventHandler {
    internal fun createMergeRequests(
        request: UpdateRequest
    ): List<MergeRequest> = request.getMergeRequestInfo()
        .map { mergeRequestInfo ->
            val projectId: GitlabProjectId = SimpleGitlabProjectId(
                mergeRequestInfo.namespace,
                mergeRequestInfo.group,
                mergeRequestInfo.projectName
            )
            val gitlabMergeRequest = gitlabService.getMergeRequest(
                projectId,
                mergeRequestInfo.mergeRequestIid
            )

            val gitlabProject = gitlabService.getProject(projectId)

            val project = projectService.createIfNotExists(
                Project(
                    gitlabProject.id,
                    gitlabProject.path,
                    projectId.group(),
                    projectId.namespace(),
                )
            )

            val telegramMessage: TelegramMessage = request.message!!
            val message: Message = messageService.createIfNotExists(
                Message(
                    telegramMessage.messageId,
                    telegramMessage.chat.id,
                    telegramMessage.text
                )
            )

            // человек, который прислал МР тоже может быть ревьювером какого-то МРа.
            // сохраним, чтобы потом можно было легко вести поиск
            val reviewer = reviewerService.createIfNotExists(
                Reviewer(
                    telegramMessage.from!!.id,
                    telegramMessage.from.username,
                )
            )

            mergeRequestService.createIfNotExists(
                MergeRequest(
                    gitlabMergeRequest.id,
                    gitlabMergeRequest.iid,
                    message.id,
                    project.id,
                    mergeRequestInfo.mergeRequestLink,
                    gitlabMergeRequest.draft,
                    gitlabMergeRequest.createdAt,
                    gitlabMergeRequest.status,
                    Instant.now(),
                    reviewer.id
                )
            )
        }
}

private data class MergeRequestInfo(
    val mergeRequestIid: Long,
    val projectName: String,
    val mergeRequestLink: String,
    val namespace: String,
    val group: String,
)

private val REGEX = Regex(
    "https://git\\.russpass\\.dev/(.*)/(.*)/(.*)/-/merge_requests/(.*)"
)
private const val MERGE_REQUEST_LINK_INDEX = 0
private const val PROJECT_NAMESPACE_INDEX = 1
private const val PROJECT_GROUP_INDEX = 2
private const val PROJECT_NAME_INDEX = 3
private const val MERGE_REQUEST_IID_GROUP_INDEX = 4

private fun UpdateRequest.getMergeRequestInfo(): List<MergeRequestInfo> {
    val text = this.message?.text ?: return listOf()
    var matchResult: MatchResult? = REGEX.find(text) ?: return listOf()

    val mergeRequestInfos = mutableListOf<MergeRequestInfo>()

    do {
        mergeRequestInfos.add(
            MergeRequestInfo(
                //!! можно спокойно ставить, так как выше вернется пустой лист если ничего не найдено
                matchResult!!.groupValues[MERGE_REQUEST_IID_GROUP_INDEX].toLong(),
                matchResult.groupValues[PROJECT_NAME_INDEX],
                matchResult.groupValues[MERGE_REQUEST_LINK_INDEX],
                matchResult.groupValues[PROJECT_NAMESPACE_INDEX],
                matchResult.groupValues[PROJECT_GROUP_INDEX],
            )
        )
        matchResult = matchResult.next()
    } while (matchResult != null)

    return mergeRequestInfos
}
