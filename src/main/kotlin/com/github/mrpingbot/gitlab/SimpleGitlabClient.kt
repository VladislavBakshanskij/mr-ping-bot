package com.github.mrpingbot.gitlab

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.mrpingbot.common.PagedResult
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequest
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequestApprove
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequestComment
import com.github.mrpingbot.gitlab.dto.response.GitlabProject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
internal class SimpleGitlabClient(
    @Value("\${gitlab.url}") private val url: String,
    @Value("\${gitlab.token}") private val token: String,
    private val objectMapper: ObjectMapper
) : GitlabClient {
    companion object {
        private const val QUERY_SEPARATOR = "&"
        private val logger: Logger = LoggerFactory.getLogger(SimpleGitlabClient::class.java)
    }

    override fun getMergeRequest(projectId: String, mergeRequestIid: Long): GitlabMergeRequest {
        val encodedProjectId = URLEncoder.encode(
            projectId,
            StandardCharsets.UTF_8
        )

        val url = "$url/projects/$encodedProjectId/merge_requests/$mergeRequestIid"
        val openConnection = URL(url).openConnection() as HttpURLConnection
        openConnection.requestMethod = HttpMethod.GET.name()
        openConnection.setRequestProperty("PRIVATE-TOKEN", token)

        logger.debug("Request URL {} HEADERS {}", url, openConnection.requestProperties)
        val response = openConnection.inputStream.use { objectMapper.readValue<GitlabMergeRequest>(it) }
        logger.debug("RESPONSE FOR URL {} BODY {}", url, response)
        openConnection.disconnect()
        return response
    }

    override fun getProject(projectId: String): GitlabProject {
        val encodedProjectId = URLEncoder.encode(
            projectId,
            StandardCharsets.UTF_8
        )

        val openConnection =
            URL("$url/projects/$encodedProjectId").openConnection() as HttpURLConnection
        openConnection.requestMethod = HttpMethod.GET.name()
        openConnection.setRequestProperty("PRIVATE-TOKEN", token)
        logger.debug("Request URL {} HEADERS {}", url, openConnection.requestProperties)
        val response = openConnection.inputStream.use { objectMapper.readValue<GitlabProject>(it) }
        logger.debug("RESPONSE FOR URL {} BODY {}", url, response)
        openConnection.disconnect()
        return response
    }

    override fun getMergeRequests(projectId: String, iids: List<Long>): List<GitlabMergeRequest> {
        val encodedProjectId = URLEncoder.encode(
            projectId,
            StandardCharsets.UTF_8
        )

        val query = iids.joinToString(QUERY_SEPARATOR) { "iids[]=$it" }
        val openConnection =
            URL("$url/projects/$encodedProjectId/merge_requests?$query").openConnection() as HttpURLConnection
        openConnection.requestMethod = HttpMethod.GET.name()
        openConnection.setRequestProperty("PRIVATE-TOKEN", token)
        logger.debug("Request URL {} HEADERS {}", url, openConnection.requestProperties)
        val response: List<GitlabMergeRequest> =
            openConnection.inputStream.use { objectMapper.readValue<List<GitlabMergeRequest>>(it) }
        logger.debug("RESPONSE FOR URL {} BODY {}", url, response)
        openConnection.disconnect()
        return response
    }

    override fun getMergeRequestComments(
        projectId: String,
        mergeRequestIid: Long
    ): List<GitlabMergeRequestComment> {
        val encodedProjectId = URLEncoder.encode(
            projectId,
            StandardCharsets.UTF_8
        )

        val openConnection =
            URL("$url/projects/$encodedProjectId/merge_requests/$mergeRequestIid/notes?per_page=100").openConnection() as HttpURLConnection
        openConnection.requestMethod = HttpMethod.GET.name()
        openConnection.setRequestProperty("PRIVATE-TOKEN", token)
        logger.debug("Request URL {} HEADERS {}", url, openConnection.requestProperties)
        val response: List<GitlabMergeRequestComment> =
            openConnection.inputStream.use { objectMapper.readValue<List<GitlabMergeRequestComment>>(it) }
        logger.debug("RESPONSE FOR URL {} BODY {}", url, response)
        openConnection.disconnect()
        return response
    }

    override fun getMergeRequestsByStatus(
        projectId: String,
        statutes: List<String>,
        page: Int,
        pageSize: Int
    ): PagedResult<GitlabMergeRequest> {
        val encodedProjectId = URLEncoder.encode(
            projectId,
            StandardCharsets.UTF_8
        )

        val query = statutes.joinToString(QUERY_SEPARATOR) { "state=$it" }

        val uri = "$url/projects/$encodedProjectId/merge_requests/?$query&page=$page&per_page=$pageSize"
        val openConnection =
            URL(uri).openConnection() as HttpURLConnection
        openConnection.requestMethod = HttpMethod.GET.name()
        openConnection.setRequestProperty("PRIVATE-TOKEN", token)
        logger.debug("Request URL {} HEADERS {}", uri, openConnection.requestProperties)
        val response: List<GitlabMergeRequest> =
            openConnection.inputStream.use { objectMapper.readValue<List<GitlabMergeRequest>>(it) }
        logger.debug("RESPONSE FOR URL {} BODY {}", uri, response)
        val total = openConnection.getHeaderFieldLong("x-total", 0)
        openConnection.disconnect()
        return PagedResult(
            page,
            pageSize,
            total,
            response
        )
    }

    override fun getMergeRequestApprovals(
        projectId: String,
        mergeRequestIid: Long
    ): GitlabMergeRequestApprove {
        val encodedProjectId = URLEncoder.encode(
            projectId,
            StandardCharsets.UTF_8
        )

        val openConnection =
            URL("$url/projects/$encodedProjectId/merge_requests/$mergeRequestIid/approvals").openConnection() as HttpURLConnection
        openConnection.requestMethod = HttpMethod.GET.name()
        openConnection.setRequestProperty("PRIVATE-TOKEN", token)
        logger.debug("Request URL {} HEADERS {}", url, openConnection.requestProperties)
        val response: GitlabMergeRequestApprove =
            openConnection.inputStream.use { objectMapper.readValue<GitlabMergeRequestApprove>(it) }
        logger.debug("RESPONSE FOR URL {} BODY {}", url, response)
        openConnection.disconnect()
        return response
    }
}
