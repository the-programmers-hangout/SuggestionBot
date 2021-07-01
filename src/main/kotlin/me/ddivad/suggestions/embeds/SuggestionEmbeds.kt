package me.ddivad.suggestions.embeds

import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.rest.Image
import com.gitlab.kordlib.rest.builder.message.EmbedBuilder
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.jakejmattson.discordkt.api.extensions.addField
import me.jakejmattson.discordkt.api.extensions.addInlineField
import me.jakejmattson.discordkt.api.extensions.toSnowflake
import java.awt.Color

suspend fun EmbedBuilder.createSuggestionEmbed(guild: Guild, suggestion: Suggestion) {
    val author = guild.kord.getUser(suggestion.author.toSnowflake()) ?: return

    author {
        icon = author.avatar.url
        name = "Suggestion from ${author.tag}"
    }
    thumbnail {
        url = guild.getIconUrl(Image.Format.PNG) ?: ""
    }
    color = Color.YELLOW
    description = suggestion.suggestion
    addField("Status", "${suggestion.status}")
    footer {
        text = "Suggestion ID: ${suggestion.id}"
    }
}

suspend fun EmbedBuilder.updateSuggestionEmbed(guild: Guild, suggestion: Suggestion) {
    val author = guild.kord.getUser(suggestion.author.toSnowflake()) ?: return

    author {
        icon = author.avatar.url
        name = "Suggestion from ${author.tag}"
    }

    thumbnail {
        url = guild.getIconUrl(Image.Format.PNG) ?: ""
    }
    color = when (suggestion.status) {
        SuggestionStatus.NEW -> Color.GREEN
        SuggestionStatus.POSTED -> Color.YELLOW
        SuggestionStatus.UNDER_REVIEW -> Color.ORANGE
        SuggestionStatus.IMPLEMENTED -> Color.MAGENTA
        SuggestionStatus.REJECTED -> Color.RED
    }
    description = suggestion.suggestion

    addField("Status", "${suggestion.status}")

    val upvotes = suggestion.upvotes.size
    val downvotes = suggestion.downvotes.size
    val totalvotes = upvotes + downvotes
    if (suggestion.status == SuggestionStatus.UNDER_REVIEW || suggestion.status == SuggestionStatus.REJECTED) {
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

