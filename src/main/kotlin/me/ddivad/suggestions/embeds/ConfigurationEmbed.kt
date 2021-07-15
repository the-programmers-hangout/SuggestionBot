package me.ddivad.suggestions.embeds

import dev.kord.common.kColor
import dev.kord.core.entity.Guild
import dev.kord.rest.Image
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.x.emoji.Emojis
import me.ddivad.suggestions.dataclasses.GuildConfiguration
import java.awt.Color

suspend fun EmbedBuilder.createConfigurationEmbed(guild: Guild, config: GuildConfiguration) {
    val discord = guild.kord
    thumbnail {
        url = guild.getIconUrl(Image.Format.PNG) ?: ""
    }
    color = Color.MAGENTA.kColor
    title = "Configuration"
    description = """
        Admin Role: ${guild.getRole(config.adminRoleId).mention}
        Staff Role: ${guild.getRole(config.staffRoleId).mention}
        Suggestion Role: ${guild.getRole(config.requiredSuggestionRole).mention}
        Suggestion Channel: ${discord.getChannel(config.suggestionChannel)?.mention}
        Suggestion Review Channel: ${discord.getChannel(config.suggestionReviewChannel)?.mention}
        Always Show Votes : ${if(config.showVotes) Emojis.whiteCheckMark else Emojis.x}
        Remove Voting Reactions : ${if(config.removeVoteReactions) Emojis.whiteCheckMark else Emojis.x}
        Send DM Confirmation : ${if(config.sendVotingDM) Emojis.whiteCheckMark else Emojis.x}
    """.trimIndent()
}
