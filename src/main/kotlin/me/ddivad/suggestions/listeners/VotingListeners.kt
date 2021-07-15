package me.ddivad.suggestions.listeners

import dev.kord.common.annotation.KordPreview
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.x.emoji.Emojis
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.embeds.createVotingConfirmation
import me.ddivad.suggestions.services.SuggestionService
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.discordkt.api.extensions.sendPrivateMessage

@KordPreview
@Suppress("unused")
fun onVotingReactionAdded(configuration: Configuration, suggestionService: SuggestionService) = listeners {
    on<ReactionAddEvent> {
        if (this.user == this.kord.getSelf()) return@on
        val guild = guild?.asGuildOrNull() ?: return@on
        val guildConfiguration = configuration[guild.id] ?: return@on
        val suggestion = suggestionService.findSuggestionByMessageId(guild, messageId) ?: return@on

        when (this.emoji.name) {
            Emojis.thumbsup.unicode -> {
                if (guildConfiguration.removeVoteReactions) {
                    message.deleteReaction(userId, emoji)
                }
                if (user.id in suggestion.upvotes) return@on
                with(suggestionService) {
                    recordUpvote(guild, user.asUser(), messageId)
                    updateStatus(guild, suggestion, suggestion.status)
                }
                if (guildConfiguration.sendVotingDM) {
                    user.sendPrivateMessage { createVotingConfirmation(guild, suggestion, guildConfiguration, emoji) }
                }
            }

            Emojis.thumbdown.unicode -> {
                if (guildConfiguration.removeVoteReactions) {
                    message.deleteReaction(userId, emoji)
                }
                if (user.id in suggestion.downvotes) return@on
                with(suggestionService) {
                    recordDownvote(guild, user.asUser(), messageId)
                    updateStatus(guild, suggestion, suggestion.status)
                }
                if (guildConfiguration.sendVotingDM) {
                    user.sendPrivateMessage { createVotingConfirmation(guild, suggestion, guildConfiguration, emoji) }
                }
            }
        }
    }
}