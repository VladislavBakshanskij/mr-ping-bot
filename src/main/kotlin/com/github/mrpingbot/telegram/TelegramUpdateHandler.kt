package com.github.mrpingbot.telegram

import com.github.mrpingbot.gitlab.dto.request.GetMergeRequest
import com.github.mrpingbot.gitlab.dto.request.GetProjectRequest
import com.github.mrpingbot.gitlab.GitlabService
import com.github.mrpingbot.mergeRequest.MergeRequest
import com.github.mrpingbot.mergeRequest.MergeRequestService
import com.github.mrpingbot.project.Project
import com.github.mrpingbot.project.ProjectService
import com.github.mrpingbot.telegram.dto.request.UpdateRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class TelegramUpdateHandler(
    private val gitlabService: GitlabService,
    private val projectService: ProjectService,
    private val mergeRequestService: MergeRequestService,
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(TelegramUpdateHandler::class.java)
    }

    @Transactional
    fun handleUpdate(request: UpdateRequest) {
        val mergeRequestInfos = request.getMergeRequestInfo()
        if (mergeRequestInfos.isEmpty()) {
            return
        }

        for (mergeRequestInfo in mergeRequestInfos) {
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

            val mergeRequest = mergeRequestService.createIfNotExists(
                MergeRequest(
                    gitlabMergeRequest.id,
                    gitlabMergeRequest.iid,
                    request.message!!.chat.id,
                    request.message.messageId,
                    project.id,
                    mergeRequestInfo.mergeRequestLink,
                    gitlabMergeRequest.draft,
                    gitlabMergeRequest.createdAt,
                    gitlabMergeRequest.status,
                    Instant.now(),
                )
            )

            log.info("Complete handle merge request update. Merge request = {}", mergeRequest)
        }
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
