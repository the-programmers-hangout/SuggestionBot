package me.ddivad.suggestions.embeds

import dev.kord.common.kColor
import dev.kord.core.entity.Guild
import dev.kord.rest.Image
import dev.kord.rest.builder.message.EmbedBuilder
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.jakejmattson.discordkt.api.extensions.addField
import java.awt.Color

suspend fun EmbedBuilder.createSuggestionReviewEmbed(guild: Guild, suggestion: Suggestion) {
    val author = guild.kord.getUser(suggestion.author) ?: return

    author {
        icon = author.avatar.url
        name = "Suggestion from ${author.tag}"
    }
    thumbnail {
        url = guild.getIconUrl(Image.Format.PNG) ?: ""
    }
    color = Color.GREEN.kColor
    description = suggestion.suggestion
    addField("Status", "${suggestion.status}")

    footer {
        text = "Suggestion ID: ${suggestion.id}"
    }
}

suspend fun EmbedBuilder.updateSuggestionReviewEmbed(guild: Guild, suggestion: Suggestion) {
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

    val upvotes = suggestion.upvotes.size
    val downvotes = suggestion.downvotes.size
    val totalvotes = upvotes + downvotes

    if (suggestion.status != SuggestionStatus.NEW) {
        field {
            name = "Votes"
            value = """
                Opinion: ${upvotes - downvotes}
                Upvotes: $upvotes `${upvotes / totalvotes * 100}%`
                Downvotes: $downvotes ` ${downvotes / totalvotes * 100} %`
            """.trimIndent()
        }
    }

    footer {
        text = "Suggestion ID: ${suggestion.id}"
    }
}