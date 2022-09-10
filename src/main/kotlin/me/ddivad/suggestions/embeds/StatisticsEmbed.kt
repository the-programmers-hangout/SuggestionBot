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
        ${
        if (configuration.guildConfigurations.size > 1)
            "**Overall**\n" +
                    "> Suggestions: ${statistics?.overall?.suggestions}\n" +
                    "> Votes: ${statistics?.overall?.votes}\n" +
                    "> Upvotes: ${statistics?.overall?.upVotes} `${statistics?.overall?.upvotePercentage}%`\n" +
                    "> Downvotes: ${statistics?.overall?.downVotes} `${statistics?.overall?.downVotePercentage}%`" else ""
    }
       
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