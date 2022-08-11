package me.ddivad.suggestions.commands

import dev.kord.core.entity.channel.TextChannel
import me.ddivad.suggestions.conversations.ConfigurationConversation
import me.ddivad.suggestions.dataclasses.BotPermissions
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.embeds.createConfigurationEmbed
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.RoleArg
import me.jakejmattson.discordkt.commands.commands

@Suppress("unused")
fun guildConfigCommands(configuration: Configuration) = commands("Setup", BotPermissions.Admin) {
    text("setup") {
        description = "Configure a guild to use this bot."
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

    text("setstaffrole") {
        description = "Set the bot staff role."
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

    text("setadminrole") {
        description = "Set the bot admin role."
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

    text("setsuggstionrole") {
        description = "Set the minimum required role to make a suggestion."
        execute(RoleArg) {
            if (!configuration.hasGuildConfig(guild.id)) {
                respond("Please run the **configure** command to set this initially.")
                return@execute
            }
            val role = args.first
            configuration[guild.id]?.requiredSuggestionRole = role.id
            configuration.save()
            respond("Role set to: **${role.name}**")
        }
    }

    text("setChannel") {
        description = "Set the review or public channel to be used for suggestions."
        execute(ChoiceArg("Channel", "public", "review"), ChannelArg<TextChannel>("Channel")) {
            if (!configuration.hasGuildConfig(guild.id)) {
                respond("Please run the **configure** command to set this initially.")
                return@execute
            }
            val (option, channel) = args
            val config = configuration[guild.id]
            when (option.lowercase()) {
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

    text("configuration") {
        description = "Set the review or public channel to be used for suggestions."
        requiredPermissions = BotPermissions.Staff
        execute {
            val config = configuration[guild.id] ?: return@execute
            respond { createConfigurationEmbed(guild, config) }
        }
    }
}