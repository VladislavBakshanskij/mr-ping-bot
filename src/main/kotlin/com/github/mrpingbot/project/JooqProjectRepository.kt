package com.github.mrpingbot.project

import com.github.jooq.tables.records.ProjectsRecord
import com.github.jooq.tables.references.PROJECTS
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Repository
internal class JooqProjectRepository(private val dslContext: DSLContext) : ProjectRepository {
    private val mapper: (Record) -> Project = {
        val projectsRecord = it as ProjectsRecord
        Project(
            projectsRecord.id,
            projectsRecord.name,
            projectsRecord.group,
            projectsRecord.namespace
        )
    }

    @Transactional(propagation = Propagation.MANDATORY)
    override fun save(project: Project): Project {
        dslContext.insertInto(PROJECTS)
            .set(PROJECTS.ID, project.id)
            .set(PROJECTS.NAME, project.name)
            .set(PROJECTS.GROUP, project.group)
            .set(PROJECTS.NAMESPACE, project.namespace)
            .execute()

        return project
    }

    override fun findById(
        id: Long
    ): Project? = dslContext.selectFrom(PROJECTS)
        .where(PROJECTS.ID.equal(id))
        .fetchOne()
        ?.map(mapper)

    override fun findAll(): List<Project> = dslContext.selectFrom(PROJECTS)
        .fetch()
        .map(mapper)
}
