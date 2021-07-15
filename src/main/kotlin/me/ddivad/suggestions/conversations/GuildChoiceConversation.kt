package me.ddivad.suggestions.conversations

import dev.kord.common.kColor
import dev.kord.core.entity.Guild
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.services.SuggestionService
import me.jakejmattson.discordkt.api.arguments.IntegerRangeArg
import me.jakejmattson.discordkt.api.conversations.conversation
import java.awt.Color

fun guildChoiceConversation(
    guilds: List<Guild>,
    suggestionMessage: String,
    suggestionService: SuggestionService,
    configuration: Configuration
) = conversation {
    val guildIndex = promptEmbed(IntegerRangeArg(1, guilds.size)) {
        title = "Select Server"
        description = "Respond with the server you want your suggestion to be posted."
        thumbnail {
            url = discord.kord.getSelf().avatar.url
        }
        color = Color.MAGENTA.kColor
        guilds.toList().forEachIndexed { index, guild ->
            field {
                name = "${index + 1}) ${guild.name}"
            }
        }
    } - 1

    val guild = guilds[guildIndex]
    val guildConfiguration = configuration[guild.id] ?: return@conversation

    if (user.asMember(guild.id).roleIds.contains(guildConfiguration.requiredSuggestionRole)) {
        val nextId: Int =
            if (guildConfiguration.suggestions.isEmpty()) 1 else guildConfiguration.suggestions.maxByOrNull { it.id }!!.id + 1
        val suggestion = Suggestion(user.id, suggestionMessage, id = nextId)
        suggestionService.addSuggestion(guild, suggestion)
        respond("Suggestion added to the pool. If accepted, it will appear in ${guild.getChannel(guildConfiguration.suggestionChannel).mention}")
    } else respond("Sorry, you don't meet the role requirements to post a suggestion.")
}
