package me.ddivad.suggestions.embeds

import dev.kord.common.kColor
import dev.kord.core.entity.Guild
import dev.kord.rest.Image
import dev.kord.rest.builder.message.EmbedBuilder
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.util.getTotalCounts
import java.awt.Color

fun EmbedBuilder.createStatsEmbed(guild: Guild, configuration: Configuration) {
    val guildIcon = guild.getIconUrl(Image.Format.PNG) ?: ""
    val statistics = getTotalCounts(guild, configuration)
    thumbnail {
        url = guildIcon
    }
    color = Color.MAGENTA.kColor
    title = "Suggestion Stats"
    description = """
        **Overall**
        > Suggestions: ${statistics?.overall?.suggestions}
        > Votes: ${statistics?.overall?.votes}
        > Upvotes: ${statistics?.overall?.upVotes} `${statistics?.overall?.upvotePercentage}%`
        > Downvotes: ${statistics?.overall?.downVotes} `${statistics?.overall?.downVotePercentage}%`
         
        **Guild**
        > Suggestions: ${statistics?.guild?.suggestions}
        > Votes: ${statistics?.guild?.votes}
        > Upvotes: ${statistics?.guild?.upVotes} `${statistics?.guild?.upvotePercentage}%`
        > Downvotes: ${statistics?.guild?.downVotes} `${statistics?.guild?.downVotePercentage}%`
    """.trimIndent()

    footer {
        icon = guildIcon
        text = guild.name
    }
}