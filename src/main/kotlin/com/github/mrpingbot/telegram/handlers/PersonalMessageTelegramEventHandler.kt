package com.github.mrpingbot.telegram.handlers

import com.github.mrpingbot.gitlab.GitlabService
import com.github.mrpingbot.gitlab.dto.request.GetMergeRequest
import com.github.mrpingbot.gitlab.dto.request.GetProjectRequest
import com.github.mrpingbot.mergeRequest.MergeRequest
import com.github.mrpingbot.mergeRequest.MergeRequestService
import com.github.mrpingbot.message.Message
import com.github.mrpingbot.message.MessageService
import com.github.mrpingbot.project.Project
import com.github.mrpingbot.project.ProjectService
import com.github.mrpingbot.rewiever.Reviewer
import com.github.mrpingbot.rewiever.ReviewerService
import com.github.mrpingbot.telegram.TelegramService
import com.github.mrpingbot.telegram.dto.request.UpdateRequest
import com.github.mrpingbot.utils.replaceAllNotLetter
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*

@Component
class PersonalMessageTelegramEventHandler(
    private val gitlabService: GitlabService,
    private val messageService: MessageService,
    private val projectService: ProjectService,
    private val reviewerService: ReviewerService,
    private val telegramService: TelegramService,
    private val mergeRequestService: MergeRequestService,
    @Value("\${telegram.code-review-chat-id:-835343898}") private val chatId: Long,
) : TelegramEventHandler {

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

    private fun createMergeRequests(
        request: UpdateRequest
    ): List<MergeRequest> = request.getMergeRequestInfo()
        .map { mergeRequestInfo ->
            val gitlabMergeRequest = gitlabService.getMergeRequest(
                GetMergeRequest(
                    mergeRequestInfo.namespace,
                    mergeRequestInfo.group,
                    mergeRequestInfo.projectName,
                    mergeRequestInfo.mergeRequestIid
                )
            )

            val gitlabProject = gitlabService.getProject(
                GetProjectRequest(
                    mergeRequestInfo.namespace,
                    mergeRequestInfo.group,
                    mergeRequestInfo.projectName
                )
            )

            val project = projectService.createIfNotExists(
                Project(
                    gitlabProject.id,
                    gitlabProject.path,
                    gitlabProject.getGroupName(),
                    gitlabProject.getNamespaceName(),
                )
            )

            val message: Message = messageService.createIfNotExists(
                Message(
                    request.message!!.messageId,
                    chatId,
                    request.message.text
                )
            )

            // человек, который прислал МР тоже может быть ревьювером какого-то МРа.
            // сохраним, чтобы потом можно было легко вести поиск
            reviewerService.createIfNotExists(
                Reviewer(
                    request.message.from!!.id,
                    request.message.from.username,
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
