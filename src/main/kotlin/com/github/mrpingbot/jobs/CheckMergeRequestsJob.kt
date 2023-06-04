package com.github.mrpingbot.jobs

import com.github.mrpingbot.gitlab.GitlabService
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequestCommentType
import com.github.mrpingbot.mergeRequest.MergeRequest
import com.github.mrpingbot.mergeRequest.MergeRequestService
import com.github.mrpingbot.mergeRequestNotifications.MergeRequestNotifications
import com.github.mrpingbot.mergeRequestNotifications.MergeRequestNotificationsService
import com.github.mrpingbot.project.ProjectService
import com.github.mrpingbot.rewiever.ReviewerService
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
    @Value("\${job.check-merge-request.statuses}") private val statuses: List<String>
) {
    @Scheduled(cron = "\${job.check-merge-request.cron}")
    fun checkMergeRequestStatus() {
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
                        .map { MergeRequestNotifications(mergeRequest.id, it.id) }
                        .forEach { mergeRequestNotificationsService.createIfNotExists(it) }

                    if (gitlabMergeRequest.status != mergeRequest.status) {
                        mergeRequestService.update(mergeRequest.updateStatus(gitlabMergeRequest.status))
                    }
                }
            }
    }
}