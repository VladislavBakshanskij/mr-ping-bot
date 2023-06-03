package com.github.mrpingbot.project

class ProjectNotFoundException(override val message: String) : RuntimeException(message) {
    companion object {
        fun ofId(id: Long): ProjectNotFoundException = ProjectNotFoundException("Проект с id $id не найден")
    }
}
