package me.ddivad.suggestions.embeds

import dev.kord.common.kColor
import dev.kord.core.entity.Guild
import dev.kord.rest.Image
import dev.kord.rest.builder.message.EmbedBuilder
import me.ddivad.suggestions.dataclasses.GuildConfiguration
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.ddivad.suggestions.util.Vote
import me.ddivad.suggestions.util.getVoteCounts
import me.jakejmattson.discordkt.extensions.addField
import me.jakejmattson.discordkt.extensions.pfpUrl
import java.awt.Color

suspend fun EmbedBuilder.createSuggestionEmbed(guild: Guild, suggestion: Suggestion, config: GuildConfiguration) {
    val author = guild.kord.getUser(suggestion.author) ?: return

    author {
        icon = author.pfpUrl
        name = "Suggestion from ${author.tag}"
    }

    thumbnail {
        url = guild.getIconUrl(Image.Format.PNG) ?: ""
    }
    color = when (suggestion.status) {
        SuggestionStatus.NEW -> Color.GREEN.kColor
        SuggestionStatus.PUBLISHED -> Color.YELLOW.kColor
        SuggestionStatus.UNDER_REVIEW -> Color.GREEN.kColor
        SuggestionStatus.IMPLEMENTED -> Color.MAGENTA.kColor
        SuggestionStatus.REJECTED -> Color.RED.kColor
    }
    description = suggestion.suggestion

    addField("Status", "${suggestion.status}")

    val voteInfo = getVoteCounts(suggestion)

    if (hasVotes(voteInfo) && (config.showVotes || suggestion.status != SuggestionStatus.PUBLISHED)) {
        field {
            name = "Votes"
            value = """
                Opinion: ${voteInfo.opinion}
                Upvotes: ${voteInfo.upVotes} `${voteInfo.upvotePercentage}%`
                Downvotes: ${voteInfo.downVotes} `${voteInfo.downVotePercentage}%`
            """.trimIndent()
        }
    }

    if (config.removeVoteReactions && suggestion.status == SuggestionStatus.PUBLISHED) {
        field {
            value =
                "Note: reactions are removed after voting, but all votes are counted. Results will be made available later."
        }
    }

    footer {
        text = "Suggestion ID: ${suggestion.id}"
    }
}

private fun hasVotes(vote: Vote): Boolean {
    return vote.upVotes > 0 || vote.downVotes > 0
}