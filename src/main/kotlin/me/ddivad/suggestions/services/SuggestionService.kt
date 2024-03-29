package me.ddivad.suggestions.services

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.getChannelOf
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.modify.embed
import dev.kord.x.emoji.Emojis
import dev.kord.x.emoji.toReaction
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.ddivad.suggestions.embeds.createSuggestionEmbed
import me.ddivad.suggestions.embeds.createSuggestionReviewEmbed
import me.ddivad.suggestions.embeds.createSuggestionReviewMenu
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.createMenu

@KordPreview
@Service
class SuggestionService(
    private val configuration: Configuration,
    private val discord: Discord,
    private val statsService: BotStatsService
) {
    suspend fun addSuggestion(guild: Guild, suggestion: Suggestion) {
        val guildConfiguration = configuration[guild.id] ?: return
        val reviewChannel = guild.getChannelOf<TextChannel>(guildConfiguration.suggestionReviewChannel)
        reviewChannel.createMenu { createSuggestionReviewMenu(discord, guild, suggestion) }.let {
            suggestion.addReviewMessageId(it.id)
            guildConfiguration.suggestions.add(suggestion)
            statsService.suggestionAdded(guild)
            configuration.save()
        }
    }

    fun recordUpvote(guild: Guild, user: User, messageId: Snowflake) {
        val suggestion = this.findSuggestionByMessageId(guild, messageId) ?: return
        suggestion.downvotes.removeIf { it == user.id }
        if (user.id !in suggestion.upvotes) {
            suggestion.upvotes.add(user.id)
            statsService.upvoteAdded(guild)
        }
        configuration.save()
    }

    fun recordDownvote(guild: Guild, user: User, messageId: Snowflake) {
        val suggestion = this.findSuggestionByMessageId(guild, messageId) ?: return
        suggestion.upvotes.removeIf { it == user.id }
        if (user.id !in suggestion.downvotes) {
            suggestion.downvotes.add(user.id)
            statsService.downvoteAdded(guild)
        }
        configuration.save()
    }

    suspend fun updateStatus(guild: Guild, suggestion: Suggestion, status: SuggestionStatus) {
        val guildConfiguration = configuration[guild.id] ?: return
        suggestion.status = status

        val reviewMessage = this.getReviewMessage(guild, suggestion.reviewMessageId)
        val suggestionMessage = this.getPublishedMessage(guild, suggestion.publishedMessageId)
        val suggestionChannel = guild.getChannelOf<TextChannel>(guildConfiguration.suggestionChannel)

        when (suggestion.status) {
            SuggestionStatus.PUBLISHED -> {
                if (suggestion.publishedMessageId == null) {
                    suggestionChannel.createEmbed { createSuggestionEmbed(guild, suggestion, guildConfiguration) }.let {
                        it.addReaction(Emojis.thumbsup.toReaction())
                        it.addReaction(Emojis.thumbdown.toReaction())
                        suggestion.addPublishMessageId(it.id)
                    }
                }
                if (guildConfiguration.showVotes) {
                    suggestionMessage?.edit {
                        this.embed {
                            createSuggestionEmbed(
                                guild,
                                suggestion,
                                guildConfiguration
                            )
                        }
                    }
                }
                reviewMessage?.edit { this.embed { createSuggestionReviewEmbed(guild, suggestion) } }
            }

            in setOf(SuggestionStatus.UNDER_REVIEW, SuggestionStatus.IMPLEMENTED, SuggestionStatus.REJECTED) -> {
                if (suggestionMessage != null) {
                    suggestionMessage.deleteAllReactions()
                    suggestionMessage.edit {
                        this.embed {
                            createSuggestionEmbed(
                                guild,
                                suggestion,
                                guildConfiguration
                            )
                        }
                    }
                } else if (suggestion.status == SuggestionStatus.IMPLEMENTED) {
                    suggestionChannel.createEmbed { createSuggestionEmbed(guild, suggestion, guildConfiguration) }.let {
                        suggestion.addPublishMessageId(it.id)
                    }
                }
                suggestionMessage?.deleteAllReactions()
                suggestionMessage?.edit { this.embed { createSuggestionEmbed(guild, suggestion, guildConfiguration) } }
                reviewMessage?.edit { this.embed { createSuggestionReviewEmbed(guild, suggestion) } }
            }

            else -> {}
        }
        configuration.save()
    }

    suspend fun resetVotes(guild: Guild, suggestion: Suggestion) {
        val reviewMessage = this.getReviewMessage(guild, suggestion.reviewMessageId)
        val suggestionMessage = this.getPublishedMessage(guild, suggestion.publishedMessageId)
        with(configuration) {
            suggestionMessage?.delete()
            suggestion.reset()
            save()
        }
        reviewMessage?.edit { this.embed { createSuggestionReviewEmbed(guild, suggestion) } }
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

    suspend fun resetSuggestionInteractions(guild: Guild, suggestion: Suggestion): Snowflake {
        val guildConfiguration = configuration[guild.id]
        val reviewChannel = guild.getChannelOf<TextChannel>(guildConfiguration!!.suggestionReviewChannel)
        getReviewMessage(guild, suggestion.reviewMessageId)?.delete()
        return reviewChannel.createMenu { createSuggestionReviewMenu(discord, guild, suggestion) }.let {
            return@let it.id
        }
    }
}