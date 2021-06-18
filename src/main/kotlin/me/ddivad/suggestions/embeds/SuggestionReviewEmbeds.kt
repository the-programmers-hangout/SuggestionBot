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

suspend fun EmbedBuilder.createSuggestionReviewEmbed(guild: Guild, suggestion: Suggestion) {
    val author = guild.kord.getUser(suggestion.author.toSnowflake()) ?: return
    thumbnail {
        url = guild.getIconUrl(Image.Format.PNG) ?: ""
    }
    title = "New Suggestion - ${suggestion.id}"
    color = Color.GREEN
    description = suggestion.suggestion
    footer {
        icon = author.avatar.url
        text = author.tag
    }
    addField("Status", "${suggestion.status}")
}

suspend fun EmbedBuilder.updateSuggestionReviewEmbed(guild: Guild, suggestion: Suggestion) {
    val author = guild.kord.getUser(suggestion.author.toSnowflake()) ?: return
    thumbnail {
        url = guild.getIconUrl(Image.Format.PNG) ?: ""
    }
    title = "Suggestion - ${suggestion.id}"
    color = when (suggestion.status) {
        SuggestionStatus.NEW -> Color.GREEN
        SuggestionStatus.POSTED -> Color.YELLOW
        SuggestionStatus.UNDER_REVIEW -> Color.ORANGE
        SuggestionStatus.IMPLEMENTED -> Color.MAGENTA
        SuggestionStatus.REJECTED -> Color.RED
    }
    description = suggestion.suggestion

    if (suggestion.status != SuggestionStatus.NEW) {
        addInlineField("Upvotes", "${suggestion.upvotes.size}")
        addInlineField("Downvotes", "${suggestion.downvotes.size}")
    }

    footer {
        icon = author.avatar.url
        text = author.tag
    }
    addField("Status", "${suggestion.status}")
}