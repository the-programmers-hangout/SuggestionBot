package me.ddivad.suggestions.services

import dev.kord.common.entity.ButtonStyle
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.TextChannel
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.toReaction
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.ddivad.suggestions.embeds.createSuggestionEmbed
import me.ddivad.suggestions.embeds.createSuggestionReviewEmbed
import me.ddivad.suggestions.embeds.updateSuggestionEmbed
import me.ddivad.suggestions.embeds.updateSuggestionReviewEmbed
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.extensions.button
import java.util.*

@Service
class SuggestionService(private val configuration: Configuration) {
    suspend fun addSuggestion(guild: Guild, suggestion: Suggestion) {
        val guildConfiguration = configuration[guild.id] ?: return
        val reviewChannel = guild.getChannelOf<TextChannel>(guildConfiguration.suggestionReviewChannel)
        reviewChannel.createEmbed { createSuggestionReviewEmbed(guild, suggestion) }.let {
            it.addReaction(Emojis.whiteCheckMark.toReaction())
            it.addReaction(Emojis.x.toReaction())
            suggestion.addReviewMessageId(it.id)
            guildConfiguration.suggestions.add(suggestion)
            configuration.save()
        }
    }

    fun recordUpvote(guild: Guild, user: User, messageId: Snowflake) {
        val suggestion = this.findSuggestionByMessageId(guild, messageId) ?: return
        if (!suggestion.downvotes.contains(user.id) && !suggestion.upvotes.contains(user.id)) {
            suggestion.upvotes.add(user.id)
        }
        configuration.save()
    }

    fun recordDownvote(guild: Guild, user: User, messageId: Snowflake) {
        val suggestion = this.findSuggestionByMessageId(guild, messageId) ?: return
        if (!suggestion.upvotes.contains(user.id) && !suggestion.downvotes.contains(user.id)) {
            suggestion.downvotes.add(user.id)
        }
        configuration.save()
    }

    suspend fun updateStatus(guild: Guild, suggestion: Suggestion, status: SuggestionStatus) {
        val guildConfiguration = configuration[guild.id] ?: return
        suggestion.status = status
        configuration.save()

        val reviewMessage = this.getReviewMessage(guild, suggestion.reviewMessageId)
        val suggestionMessage = this.getPublishedMessage(guild, suggestion.publishedMessageId)

        when (suggestion.status) {
            SuggestionStatus.POSTED -> {
                if (suggestion.publishedMessageId == null) {
                    val suggestionChannel = guild.getChannelOf<TextChannel>(guildConfiguration.suggestionChannel)
                    suggestionChannel.createEmbed { createSuggestionEmbed(guild, suggestion) }.let {
                        it.addReaction(Emojis.thumbsup.toReaction())
                        it.addReaction(Emojis.thumbdown.toReaction())
                        suggestion.addPublishMessageId(it.id)
                    }
                }
                reviewMessage?.edit { this.embed { updateSuggestionReviewEmbed(guild, suggestion) } }
            }
            SuggestionStatus.UNDER_REVIEW -> {
                suggestionMessage?.deleteAllReactions()
                suggestionMessage?.edit { this.embed { updateSuggestionEmbed(guild, suggestion) } }
                reviewMessage?.edit { this.embed { updateSuggestionReviewEmbed(guild, suggestion) } }
            }
            SuggestionStatus.IMPLEMENTED -> {
                suggestionMessage?.edit { this.embed { updateSuggestionEmbed(guild, suggestion) } }
                reviewMessage?.edit { this.embed { updateSuggestionReviewEmbed(guild, suggestion) } }
            }
            SuggestionStatus.REJECTED -> {
                suggestionMessage?.edit { this.embed { updateSuggestionEmbed(guild, suggestion) } }
                reviewMessage?.edit { this.embed { updateSuggestionReviewEmbed(guild, suggestion) } }
            }
        }
    }

    private suspend fun getReviewMessage(guild: Guild, messageId: Snowflake?): Message? {
        if (messageId == null) return null
        val guildConfiguration = configuration[guild.id] ?: return null
        return messageId.let {
            guild.getChannelOf<TextChannel>(guildConfiguration.suggestionReviewChannel)
                .getMessageOrNull(it)
        }
    }

    private suspend fun getPublishedMessage(guild: Guild, messageId: Snowflake?): Message? {
        if (messageId == null) return null
        val guildConfiguration = configuration[guild.id] ?: return null
        return messageId.let {
            guild.getChannelOf<TextChannel>(guildConfiguration.suggestionChannel)
                .getMessageOrNull(it)
        }
    }

    fun findSuggestionByMessageId(guild: Guild, messageId: Snowflake): Suggestion? {
        val guildConfiguration = configuration[guild.id] ?: return null
        return guildConfiguration.suggestions.firstOrNull { it.reviewMessageId == messageId || it.publishedMessageId == messageId }
    }

    fun findSuggestionById(guild: Guild, suggestionId: Int): Suggestion? {
        val guildConfiguration = configuration[guild.id] ?: return null
        return guildConfiguration.suggestions.find { it.id == suggestionId }
    }
}