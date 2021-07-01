package me.ddivad.suggestions.services

import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.core.behavior.getChannelOf
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.kordx.emoji.Emojis
import com.gitlab.kordlib.kordx.emoji.addReaction
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.ddivad.suggestions.embeds.createSuggestionEmbed
import me.ddivad.suggestions.embeds.createSuggestionReviewEmbed
import me.ddivad.suggestions.embeds.updateSuggestionEmbed
import me.ddivad.suggestions.embeds.updateSuggestionReviewEmbed
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.extensions.toSnowflake
import me.jakejmattson.discordkt.api.extensions.toSnowflakeOrNull

@Service
class SuggestionService(private val configuration: Configuration) {
    suspend fun addSuggestion(guild: Guild, suggestion: Suggestion) {
        val guildConfiguration = configuration[guild.id.longValue] ?: return
        val reviewChannel = guild.getChannelOf<TextChannel>(guildConfiguration.suggestionReviewChannel.toSnowflake())
        reviewChannel.createEmbed { createSuggestionReviewEmbed(guild, suggestion) }.let {
            it.addReaction(Emojis.whiteCheckMark)
            it.addReaction(Emojis.x)
            suggestion.addReviewMessageId(it.id.value)
            guildConfiguration.suggestions.add(suggestion)
            configuration.save()
        }
    }

    fun recordUpvote(guild: Guild, user: User, messageId: String) {
        val suggestion = this.findSuggestionByMessageId(guild, messageId) ?: return
        if (!suggestion.downvotes.contains(user.id.value) && !suggestion.upvotes.contains(user.id.value)) {
            suggestion.upvotes.add(user.id.value)
        }
        configuration.save()
    }

    fun recordDownvote(guild: Guild, user: User, messageId: String) {
        val suggestion = this.findSuggestionByMessageId(guild, messageId) ?: return
        if (!suggestion.upvotes.contains(user.id.value) && !suggestion.downvotes.contains(user.id.value)) {
            suggestion.downvotes.add(user.id.value)
        }
        configuration.save()
    }

    suspend fun updateStatus(guild: Guild, suggestion: Suggestion, status: SuggestionStatus) {
        val guildConfiguration = configuration[guild.id.longValue] ?: return
        suggestion.status = status
        configuration.save()

        val reviewMessage = this.getReviewMessage(guild, suggestion.reviewMessageId)
        val suggestionMessage = this.getPublishedMessage(guild, suggestion.publishedMessageId)
        println(suggestion.status)
        println(reviewMessage)
        when (suggestion.status) {
            SuggestionStatus.POSTED -> {
                if (suggestion.publishedMessageId == null) {
                    val suggestionChannel = guild.getChannelOf<TextChannel>(guildConfiguration.suggestionChannel.toSnowflake())
                    suggestionChannel.createEmbed { createSuggestionEmbed(guild, suggestion) }.let {
                        it.addReaction(Emojis.thumbsup)
                        it.addReaction(Emojis.thumbdown)
                        suggestion.addPublishMessageId(it.id.value)
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

    private suspend fun getReviewMessage(guild: Guild, messageId: String?): Message? {
        if (messageId == null) return null
        val guildConfiguration = configuration[guild.id.longValue] ?: return null
        return messageId.toSnowflakeOrNull()?.let {
            guild.getChannelOf<TextChannel>(guildConfiguration.suggestionReviewChannel.toSnowflake())
                .getMessageOrNull(it)
        }
    }

    private suspend fun getPublishedMessage(guild: Guild, messageId: String?): Message? {
        if (messageId == null) return null
        val guildConfiguration = configuration[guild.id.longValue] ?: return null
        return messageId.toSnowflakeOrNull()?.let {
            guild.getChannelOf<TextChannel>(guildConfiguration.suggestionChannel.toSnowflake())
                .getMessageOrNull(it)
        }
    }

    fun findSuggestionByMessageId(guild: Guild, messageId: String): Suggestion? {
        val guildConfiguration = configuration[guild.id.longValue] ?: return null
        return guildConfiguration.suggestions.firstOrNull { it.reviewMessageId == messageId || it.publishedMessageId == messageId }
    }

    fun findSuggestionById(guild: Guild, suggestionId: Int): Suggestion? {
        val guildConfiguration = configuration[guild.id.longValue] ?: return null
        return guildConfiguration.suggestions.find { it.id == suggestionId }
    }
}