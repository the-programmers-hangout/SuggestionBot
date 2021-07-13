package me.ddivad.suggestions.commands

import dev.kord.core.entity.channel.TextChannel
import me.ddivad.suggestions.conversations.ConfigurationConversation
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.Permissions
import me.ddivad.suggestions.embeds.createConfigurationEmbed
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.commands

@Suppress("unused")
fun guildConfigCommands(configuration: Configuration) = commands("Setup") {
    guildCommand("setup") {
        description = "Configure a guild to use this bot."
        requiredPermission = Permissions.ADMINISTRATOR
        execute {
            if (configuration.hasGuildConfig(guild.id)) {
                respond("Guild configuration exists. To modify it use the commands to set values.")
                return@execute
            }
            ConfigurationConversation(configuration)
                    .createConfigurationConversation(guild)
                    .startPublicly(discord, author, channel)
            respond("${guild.name} setup with the following configuration:")
            respond { configuration[guild.id]?.let { createConfigurationEmbed(guild, it) } }
        }
    }

    guildCommand("setstaffrole") {
        description = "Set the bot staff role."
        requiredPermission = Permissions.ADMINISTRATOR
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
        requiredPermission = Permissions.ADMINISTRATOR
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

    guildCommand("setChannel") {
        description = "Set the review or public channel to be used for suggestions."
        requiredPermission = Permissions.ADMINISTRATOR
        execute(ChoiceArg("Channel", "public", "review"), ChannelArg<TextChannel>("Channel")) {
            if (!configuration.hasGuildConfig(guild.id)) {
                respond("Please run the **configure** command to set this initially.")
                return@execute
            }
            val (option, channel) = args
            val config = configuration[guild.id]
            when(option.toLowerCase()) {
                "public" -> {
                    config?.suggestionChannel = channel.id
                }
                "review" -> {
                    config?.suggestionReviewChannel = channel.id
                }
            }
            configuration.save()
            respond("Set the **$option** channel to ${channel.mention}")
        }
    }

    guildCommand("configuration") {
        description = "Set the review or public channel to be used for suggestions."
        requiredPermission = Permissions.STAFF
        execute {
            val config = configuration[guild.id] ?: return@execute
            respond {createConfigurationEmbed(guild, config)}
        }
    }
}