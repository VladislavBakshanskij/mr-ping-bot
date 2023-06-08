package com.github.mrpingbot.jobs

import com.github.mrpingbot.common.PagedResult
import com.github.mrpingbot.gitlab.GitlabService
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequest
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequestCommentType
import com.github.mrpingbot.mergeRequest.MergeRequest
import com.github.mrpingbot.mergeRequest.MergeRequestService
import com.github.mrpingbot.mergeRequestNotifications.MergeRequestNotifications
import com.github.mrpingbot.mergeRequestNotifications.MergeRequestNotificationsService
import com.github.mrpingbot.message.Message
import com.github.mrpingbot.message.MessageService
import com.github.mrpingbot.project.ProjectService
import com.github.mrpingbot.rewiever.ReviewerService
import com.github.mrpingbot.telegram.TelegramService
import com.github.mrpingbot.utils.replaceAllNotLetter
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    value = ["job.enabled", "job.check-merge-request.enabled"],
    matchIfMissing = true
)
class CheckMergeRequestsJob(
    private val gitlabService: GitlabService,
    private val projectService: ProjectService,
    private val mergeRequestService: MergeRequestService,
    private val reviewerService: ReviewerService,
    private val mergeRequestNotificationsService: MergeRequestNotificationsService,
    private val telegramService: TelegramService,
    private val messageService: MessageService,
    @Value("\${job.check-merge-request.statuses}") private val statuses: List<String>,
) {
    @Scheduled(cron = "\${job.check-merge-request.cron}")
    fun checkMergeRequestStatus() {
        createMergeRequestsFromGitlab()
        updateExistsMergeRequests()
    }

    private fun createMergeRequestsFromGitlab() {
        val reviewers = reviewerService.getAllNames()
        val projects = projectService.getAll()
        for (project in projects) {
            var gitlabMergeRequests: PagedResult<GitlabMergeRequest>

            var page = GitlabService.START_PAGE

            do {
                gitlabMergeRequests = gitlabService.getMergeRequestsByStatus(
                    project,
                    statuses,
                    page,
                    50
                )

                gitlabMergeRequests.items.filter { reviewers.isEmpty() || reviewers.contains(it.author.username) }
                    .filterNot { mergeRequestService.existsById(it.id) }
                    .map {
                        val sendMessage = telegramService.sendToCodeReviewChat(
                            """
                                    #mr #${project.name.replaceAllNotLetter("_")}
    
                                    ${it.webUrl}
                                """.trimIndent()
                        )
                        val message = messageService.createIfNotExists(
                            Message(
                                sendMessage.messageId,
                                sendMessage.chat.id,
                                sendMessage.text
                            )
                        )
                        MergeRequest(
                            it.id,
                            it.iid,
                            message.id,
                            project.id,
                            it.webUrl,
                            it.draft,
                            it.createdAt,
                            it.status,
                            it.updatedAt,
                        )
                    }
                    .forEach { mergeRequestService.createIfNotExists(it) }
                page++;
            } while (gitlabMergeRequests.items.isNotEmpty())
        }
    }

    private fun updateExistsMergeRequests() {
        val mergeRequests: List<MergeRequest> = mergeRequestService.getAllByStatuses(statuses)

        mergeRequests.groupBy { it.projectId }
            .mapKeys { projectService.getById(it.key) }
            .forEach { (project, mergeRequests) ->
                val iid2GitlabMergeRequest = gitlabService.getMergeRequests(
                    project,
                    mergeRequests.map { it.iid }
                ).groupBy { it.iid }
                    // в рамках проекта всегда будет уникальный iid
                    .mapValues { it.value.first() }

                for (mergeRequest in mergeRequests) {
                    val gitlabMergeRequest = iid2GitlabMergeRequest[mergeRequest.iid]!!
                    val usernamesApprovedMergeRequest = gitlabService.getMergeRequestApprovals(
                        project,
                        mergeRequest.iid
                    ).approvedBy
                        .map { it.user.username }

                    gitlabService.getMergeRequestsComments(
                        project,
                        mergeRequest.iid
                    ).asSequence()
                        .filter { it.author.id != gitlabMergeRequest.id }
                        .filter { it.type == GitlabMergeRequestCommentType.DIFF_NOTE }
                        .map { it.author.username }
                        // todo добавить проверку на активного пользователя
                        .distinct()
                        .mapNotNull { reviewerService.findByGitlabUsername(it) }
                        .map {
                            MergeRequestNotifications(
                                mergeRequest.id,
                                it.id,
                                usernamesApprovedMergeRequest.contains(it.gitlabUsername)
                            )
                        }
                        .forEach { mergeRequestNotificationsService.createIfNotExists(it) }

                    if (gitlabMergeRequest.status != mergeRequest.status) {
                        mergeRequestService.update(mergeRequest.updateStatus(gitlabMergeRequest.status))
                    }
                }
            }
    }
}
