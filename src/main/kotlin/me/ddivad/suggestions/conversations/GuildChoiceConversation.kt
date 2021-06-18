package me.ddivad.suggestions.conversations

import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Message
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.services.SuggestionService
import me.jakejmattson.discordkt.api.arguments.IntegerRangeArg
import me.jakejmattson.discordkt.api.dsl.conversation
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
            url = discord.api.getSelf().avatar.url
        }
        color = Color.MAGENTA
        guilds.toList().forEachIndexed { index, guild ->
            field {
                name = "${index + 1}) ${guild.name}"
            }
        }
    } - 1

    val guild = guilds[guildIndex]
    val guildConfiguration = configuration[guild.id.longValue] ?: return@conversation
    val nextId: Int =
        if (guildConfiguration.suggestions.isEmpty()) 1 else guildConfiguration.suggestions.maxByOrNull { it.id }!!.id + 1
    val suggestion = Suggestion(user.id.value, suggestionMessage, id = nextId)
    suggestionService.addSuggestion(guild, suggestion)
}
