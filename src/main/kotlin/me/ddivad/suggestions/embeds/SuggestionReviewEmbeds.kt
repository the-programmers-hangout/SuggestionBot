package me.ddivad.suggestions.embeds

import dev.kord.common.annotation.KordPreview
import dev.kord.common.kColor
import dev.kord.core.entity.Guild
import dev.kord.rest.Image
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.x.emoji.Emojis
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.ddivad.suggestions.services.SuggestionService
import me.ddivad.suggestions.util.getVoteCounts
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.MenuBuilder
import me.jakejmattson.discordkt.api.extensions.addField
import java.awt.Color

@KordPreview
suspend fun MenuBuilder.createSuggestionReviewMenu(discord: Discord, guild: Guild, suggestion: Suggestion) {
    val suggestionService = discord.getInjectionObjects(SuggestionService::class)

    page {
        createSuggestionReviewEmbed(guild, suggestion)
    }

    buttons {
        actionButton("Approve", Emojis.thumbsup) {
            val suggestion = suggestionService.findSuggestionByMessageId(guild, this.message!!.id) ?: return@actionButton
            suggestionService.updateStatus(guild, suggestion, SuggestionStatus.POSTED)
            this.acknowledgeEphemeralDeferredMessageUpdate()
        }
        actionButton("Reject", Emojis.thumbdown) {
            val suggestion = suggestionService.findSuggestionByMessageId(guild, this.message!!.id) ?: return@actionButton
            suggestionService.updateStatus(guild, suggestion, SuggestionStatus.REJECTED)
            this.acknowledgeEphemeralDeferredMessageUpdate()
        }
        actionButton("Under Review", Emojis.informationSource) {
            val suggestion = suggestionService.findSuggestionByMessageId(guild, this.message!!.id) ?: return@actionButton
            suggestionService.updateStatus(guild, suggestion, SuggestionStatus.UNDER_REVIEW)
            this.acknowledgeEphemeralDeferredMessageUpdate()
        }
        actionButton("Complete", Emojis.whiteCheckMark) {
            val suggestion = suggestionService.findSuggestionByMessageId(guild, this.message!!.id) ?: return@actionButton
            suggestionService.updateStatus(guild, suggestion, SuggestionStatus.IMPLEMENTED)
            this.acknowledgeEphemeralDeferredMessageUpdate()
        }
    }
}

suspend fun EmbedBuilder.createSuggestionReviewEmbed(guild: Guild, suggestion: Suggestion) {
    val author = guild.kord.getUser(suggestion.author) ?: return
    
    author {
        icon = author.avatar.url
        name = "Suggestion from ${author.tag}"
    }
    thumbnail {
        url = guild.getIconUrl(Image.Format.PNG) ?: ""
    }
    color = when (suggestion.status) {
        SuggestionStatus.NEW -> Color.GREEN.kColor
        SuggestionStatus.POSTED -> Color.YELLOW.kColor
        SuggestionStatus.UNDER_REVIEW -> Color.ORANGE.kColor
        SuggestionStatus.IMPLEMENTED -> Color.MAGENTA.kColor
        SuggestionStatus.REJECTED -> Color.RED.kColor
    }
    description = suggestion.suggestion
    addField("Status", "${suggestion.status}")

   val voteInfo = getVoteCounts(suggestion)

    if (suggestion.status != SuggestionStatus.NEW) {
        field {
            name = "Votes"
            value = """
                Opinion: ${voteInfo.upVotes - voteInfo.downVotes}
                Upvotes: ${voteInfo.upVotes} `${voteInfo.upvotePercentage} %`
                Downvotes: ${voteInfo.downVotes} ` ${voteInfo.downVotePercentage} %`
            """.trimIndent()
        }
    }

    footer {
        text = "Suggestion ID: ${suggestion.id}"
    }
}