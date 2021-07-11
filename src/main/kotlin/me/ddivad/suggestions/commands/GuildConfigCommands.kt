package me.ddivad.suggestions.commands

import me.ddivad.suggestions.conversations.ConfigurationConversation
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.services.PermissionLevel
import me.ddivad.suggestions.services.requiredPermissionLevel
import me.jakejmattson.discordkt.api.arguments.BooleanArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.arguments.RoleArg
import me.jakejmattson.discordkt.api.dsl.commands

@Suppress("unused")
fun guildConfigCommands(configuration: Configuration) = commands("Configuration") {
    guildCommand("setup") {
        description = "Configure a guild to use this bot."
        requiredPermissionLevel = PermissionLevel.Administrator
        execute {
            if (configuration.hasGuildConfig(guild.id)) {
                respond("Guild configuration exists. To modify it use the commands to set values.")
                return@execute
            }
            ConfigurationConversation(configuration)
                    .createConfigurationConversation(guild)
                    .startPublicly(discord, author, channel)
            respond("${guild.name} setup")
        }
    }

    guildCommand("setstaffrole") {
        description = "Set the bot staff role."
        requiredPermissionLevel = PermissionLevel.Administrator
        execute(RoleArg) {
            if (!configuration.hasGuildConfig(guild.id)) {
                respond("Please run the **configure** command to set this initially.")
                return@execute
            }
            val role = args.first
            configuration[guild.id]?.staffRoleId = role.id
            configuration.save()
            respond("Role set to: **${role.name}**")
        }
    }

    guildCommand("setadminrole") {
        description = "Set the bot admin role."
        requiredPermissionLevel = PermissionLevel.Administrator
        execute(RoleArg) {
            if (!configuration.hasGuildConfig(guild.id)) {
                respond("Please run the **configure** command to set this initially.")
                return@execute
            }
            val role = args.first
            configuration[guild.id]?.adminRoleId = role.id
            configuration.save()
            respond("Role set to: **${role.name}**")
        }
    }

    guildCommand("toggleShowVotes") {
        description = "Toggle votes being displayed on in-progress suggestions."
        requiredPermissionLevel = PermissionLevel.Administrator
        execute {
            if (!configuration.hasGuildConfig(guild.id)) {
                respond("Please run the **configure** command to set this initially.")
                return@execute
            }
            val guildConfig = configuration[guild.id] ?: return@execute
            guildConfig.showVotes = !guildConfig.showVotes
            respond("Toggled displaying votes ${if (guildConfig.showVotes) "**On**" else "**Off**"}")
        }
    }

    guildCommand("toggleRemoveReactions") {
        description = "Toggle reactions being removed on in-progress suggestions."
        requiredPermissionLevel = PermissionLevel.Administrator
        execute {
            if (!configuration.hasGuildConfig(guild.id)) {
                respond("Please run the **configure** command to set this initially.")
                return@execute
            }
            val guildConfig = configuration[guild.id] ?: return@execute
            guildConfig.removeVoteReactions = !guildConfig.removeVoteReactions
            respond("Toggled removing reactions ${if (guildConfig.removeVoteReactions) "**On**" else "**Off**"}")
        }
    }
}