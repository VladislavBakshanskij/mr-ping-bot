package com.github.mrpingbot.telegram.handlers.command

import com.github.mrpingbot.rewiever.Reviewer
import com.github.mrpingbot.rewiever.ReviewerService
import com.github.mrpingbot.telegram.dto.common.TelegramMessage
import org.springframework.stereotype.Component

@Component
class RegisterReviewerTelegramCommandHandler(
    private val reviewerService: ReviewerService
) : TelegramCommandHandler {
    companion object {
        private val GITLAB_USERNAME_REGEX: Regex = Regex("@gitlab:(\\w+)")
        private const val GITLAB_USERNAME_INDEX = 1
    }

    override fun supportCommand(): TelegramCommand = TelegramCommand.REGISTER_REVIEWER

    override fun handle(message: TelegramMessage): String {
        val text = message.text!!

        val gitlabUsername = GITLAB_USERNAME_REGEX.find(text)?.groupValues?.get(GITLAB_USERNAME_INDEX)
            ?: return "Не указано имя пользователя в gitlab"
        val telegramUser = message.from ?: return "Пользователь телеграм не был получен"
        val telegramUsername = telegramUser.username ?: "Не удалось получить имя пользователя в телеграм"
        val personalChatId = message.chat.id

        if (reviewerService.existsByGitlabUsername(gitlabUsername)) {
            return """
                Ревьювер с таким именем в gitlab уже есть. 
                Обратитесь к администратору, чтобы удалить/обновить ваши данные.
            """.trimIndent()
        }

        val reviewer = reviewerService.findById(telegramUser.id)
        if (reviewer == null) {
            reviewerService.createIfNotExists(
                Reviewer(
                    telegramUser.id,
                    telegramUsername,
                    personalChatId,
                    gitlabUsername
                )
            )

            return "Ревьювер зарегистрован"
        }

        reviewerService.update(reviewer.updatePersonalChatAndGitlabUsername(personalChatId, gitlabUsername))
        return "Данные о ревьювере были обновлены"
    }
}
