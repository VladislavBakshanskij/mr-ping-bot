package com.github.mrpingbot.project

interface ProjectRepository {
    fun save(project: Project): Project

    fun findById(id: Long): Project?

    fun findAll(): List<Project>
}