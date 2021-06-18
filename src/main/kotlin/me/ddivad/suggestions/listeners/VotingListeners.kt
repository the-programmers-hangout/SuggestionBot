package me.ddivad.suggestions.listeners

import com.gitlab.kordlib.core.event.message.ReactionAddEvent
import com.gitlab.kordlib.kordx.emoji.Emojis
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.ddivad.suggestions.services.SuggestionService
import me.jakejmattson.discordkt.api.dsl.listeners

@Suppress("unused")
fun onVotingReactionAdded(configuration: Configuration, suggestionService: SuggestionService) = listeners {
    on<ReactionAddEvent> {
        val guild = guild?.asGuildOrNull() ?: return@on
        val suggestion = suggestionService.findSuggestionByMessageId(guild, messageId.value) ?: return@on

        if (this.user == this.kord.getSelf()) return@on
        when (this.emoji.name) {
            Emojis.thumbsup.unicode -> {
                message.deleteReaction(userId, emoji)
                with(suggestionService) {
                    recordUpvote(guild, user.asUser(), messageId.value)
                    updateStatus(guild, suggestion, suggestion.status)
                }
            }

            Emojis.thumbdown.unicode -> {
                message.deleteReaction(userId, emoji)
                with(suggestionService) {
                    recordDownvote(guild, user.asUser(), messageId.value)
                    updateStatus(guild, suggestion, suggestion.status)
                }
            }

            Emojis.whiteCheckMark.unicode -> {
                val suggestion = suggestionService.findSuggestionByMessageId(guild, messageId.value) ?: return@on
                suggestionService.updateStatus(guild, suggestion, SuggestionStatus.POSTED)
            }

            Emojis.x.unicode -> {
                val suggestion = suggestionService.findSuggestionByMessageId(guild, messageId.value) ?: return@on
                suggestionService.updateStatus(guild, suggestion, SuggestionStatus.REJECTED)
            }
        }
    }
}