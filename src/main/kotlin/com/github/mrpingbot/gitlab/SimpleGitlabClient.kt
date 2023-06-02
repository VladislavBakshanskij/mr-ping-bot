package com.github.mrpingbot.gitlab

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.mrpingbot.gitlab.dto.response.GitlabMergeRequest
import com.github.mrpingbot.gitlab.dto.response.GitlabProject
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
    override fun getMergeRequest(projectNameWithNamespace: String, mergeRequestIid: Long): GitlabMergeRequest {
        val encodedProjectName = URLEncoder.encode(
            projectNameWithNamespace,
            StandardCharsets.UTF_8
        )

        val openConnection =
            URL("$url/projects/$encodedProjectName/merge_requests/$mergeRequestIid").openConnection() as HttpURLConnection
        openConnection.requestMethod = HttpMethod.GET.name()
        openConnection.setRequestProperty("PRIVATE-TOKEN", token)
        val response = openConnection.inputStream.use { objectMapper.readValue(it, GitlabMergeRequest::class.java) }
        openConnection.disconnect()
        return response
    }

    override fun getProject(projectNameWithNamespace: String): GitlabProject {
        val encodedProjectName = URLEncoder.encode(
            projectNameWithNamespace,
            StandardCharsets.UTF_8
        )

        val openConnection =
            URL("$url/projects/$encodedProjectName").openConnection() as HttpURLConnection
        openConnection.requestMethod = HttpMethod.GET.name()
        openConnection.setRequestProperty("PRIVATE-TOKEN", token)
        val response = openConnection.inputStream.use { objectMapper.readValue(it, GitlabProject::class.java) }
        openConnection.disconnect()
        return response
    }
}