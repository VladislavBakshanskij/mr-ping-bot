package com.github.mrpingbot.telegram.handlers.command

import com.github.mrpingbot.rewiever.Reviewer
import com.github.mrpingbot.rewiever.ReviewerService
import com.github.mrpingbot.telegram.dto.common.TelegramMessage
import com.github.mrpingbot.utils.getMessage
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import java.util.*

@Component
class RegisterReviewerTelegramCommandHandler(
    private val reviewerService: ReviewerService,
    private val messageSource: MessageSource,
) : TelegramCommandHandler {
    companion object {
        private val GITLAB_USERNAME_REGEX: Regex = Regex("@gitlab:(\\w+)")
        private const val GITLAB_USERNAME_INDEX = 1
    }

    override fun supportCommand(): TelegramCommand = TelegramCommand.REGISTER_REVIEWER

    override fun handle(message: TelegramMessage): String {
        val text = message.text!!

        val gitlabUsername = GITLAB_USERNAME_REGEX.find(text)?.groupValues?.get(GITLAB_USERNAME_INDEX)
            ?: return messageSource.getMessage("command.reviewer.gitlab_username_not_present")

        val telegramUser = message.from
            ?: return messageSource.getMessage("command.reviewer.telegram_user_not_received")
        val telegramUsername = telegramUser.username
            ?: return messageSource.getMessage("command.reviewer.telegram_username_not_received")

        val personalChatId = message.chat.id

        if (reviewerService.existsByGitlabUsername(gitlabUsername)) {
            return messageSource.getMessage("command.reviewer.already_exists", emptyArray(), Locale.ROOT)
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

            return messageSource.getMessage("command.reviewer.registered", emptyArray(), Locale.ROOT)
        }

        reviewerService.update(reviewer.updatePersonalChatAndGitlabUsername(personalChatId, gitlabUsername))
        return messageSource.getMessage("command.reviewer.updated", emptyArray(), Locale.ROOT)
    }
}
