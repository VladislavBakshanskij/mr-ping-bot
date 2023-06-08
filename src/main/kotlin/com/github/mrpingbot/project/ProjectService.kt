package com.github.mrpingbot.project

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProjectService(private val projectRepository: ProjectRepository) {
    @Transactional
    fun createIfNotExists(project: Project): Project {
        val foundedProject = projectRepository.findById(project.id)
        return foundedProject ?: projectRepository.save(project)
    }

    fun getById(projectId: Long): Project =
        projectRepository.findById(projectId) ?: throw ProjectNotFoundException.ofId(projectId)

    fun getAll(): List<Project> = projectRepository.findAll()
}
