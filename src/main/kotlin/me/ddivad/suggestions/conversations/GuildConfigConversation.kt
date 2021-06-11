package me.ddivad.suggestions.conversations

import com.gitlab.kordlib.core.entity.Guild
import me.ddivad.suggestions.dataclasses.Configuration
import me.jakejmattson.discordkt.api.arguments.ChannelArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.dsl.conversation

class ConfigurationConversation(private val configuration: Configuration) {
    fun createConfigurationConversation(guild: Guild) = conversation {
        val prefix = promptMessage(EveryArg, "Bot prefix:")
        val adminRole = promptMessage(RoleArg, "Admin role:")
        val staffRole = promptMessage(RoleArg, "Staff role:")
        val suggestionChannel = promptMessage(ChannelArg, "Suggestion channel:")
        val suggestionReviewChannel = promptMessage(ChannelArg, "Suggestion Review Channel:")

        configuration.setup(guild, prefix, adminRole, staffRole, suggestionChannel, suggestionReviewChannel)
    }
}