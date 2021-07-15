package me.ddivad.suggestions.embeds

import dev.kord.common.kColor
import dev.kord.core.entity.Guild
import dev.kord.core.entity.ReactionEmoji
import dev.kord.rest.Image
import dev.kord.rest.builder.message.EmbedBuilder
import me.ddivad.suggestions.dataclasses.GuildConfiguration
import me.ddivad.suggestions.dataclasses.Suggestion
import java.awt.Color

fun EmbedBuilder.createVotingConfirmation(guild: Guild, suggestion: Suggestion, config: GuildConfiguration, emoji: ReactionEmoji) {
    thumbnail {
        url = guild.getIconUrl(Image.Format.PNG) ?: ""
    }
    color = Color.MAGENTA.kColor
    title = "Vote Recorded (${emoji.name})"
    description = """
        Thanks for voting for the suggestion:
        ```
        ${suggestion.suggestion}
        ```
        Results will be made available at a later date.
    """.trimIndent()

    if (config.removeVoteReactions) {
        field {
            value = "Note: Your vote was recorded even though your reaction was removed!"
        }
    }

    footer {
        icon = guild.getIconUrl(Image.Format.PNG) ?: ""
        text = "${guild.name}"
    }
}