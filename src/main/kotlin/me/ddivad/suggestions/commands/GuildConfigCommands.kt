package me.ddivad.suggestions.commands

import dev.kord.core.entity.channel.TextChannel
import me.ddivad.suggestions.dataclasses.BotPermissions
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.embeds.createConfigurationEmbed
import me.jakejmattson.discordkt.arguments.BooleanArg
import me.jakejmattson.discordkt.arguments.ChannelArg
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.RoleArg
import me.jakejmattson.discordkt.commands.subcommand
import me.jakejmattson.discordkt.dsl.edit

@Suppress("unused")
fun guildConfigCommands(configuration: Configuration) = subcommand("Configuration", BotPermissions.Admin) {
    sub("setup", "Configure a guild to use this bot.") {
        execute(
            ChannelArg("SuggestionChannel", "Channel suggestions will be published to"),
            ChannelArg("ReviewChannel", "Suggestion review channel"),
            BooleanArg("ShowVotes", "yes", "no", "Show votes on public suggestions"),
            BooleanArg("ShowReactions", "yes", "no", "Show reactions on public suggestions during voting"),
            BooleanArg("ConfirmationDM", "yes", "no", "Send confirmation dm when voting")
        ) {
            if (configuration.hasGuildConfig(guild.id)) {
                respond("Guild configuration exists. To modify it use the commands to set values.")
                return@execute
            }
            val (suggestionChannel, suggestionReviewChannel, showVotes, showReactions, confirmationDM) = args
            val guildConfiguration = configuration.setup(
                guild,
                suggestionChannel as TextChannel,
                suggestionReviewChannel as TextChannel,
                showVotes,
                showReactions,
                confirmationDM
            )
            respondPublic("${guild.name} setup with the following configuration:") {
                if (guildConfiguration != null) {
                    createConfigurationEmbed(
                        guild, guildConfiguration
                    )
                }
            }
        }
    }

    sub("setSuggstionRole", "Set the minimum required role to make a suggestion.") {
        execute(RoleArg) {
            if (!configuration.hasGuildConfig(guild.id)) {
                respond("Please run the **configure** command to set this initially.")
                return@execute
            }
            val role = args.first
            configuration.edit { guildConfigurations[guild.id]?.requiredSuggestionRole = role.id }
            respond("Role set to: **${role.name}**")
        }
    }

    sub("setChannel", "Set the review or public channel to be used for suggestions.") {
        execute(ChoiceArg("ChannelType", "public", "review"), ChannelArg<TextChannel>("Channel")) {
            if (!configuration.hasGuildConfig(guild.id)) {
                respond("Please run the **configure** command to set this initially.")
                return@execute
            }
            val (option, channel) = args
            val config = configuration[guild.id]
            when (option.lowercase()) {
                "public" -> {
                    configuration.edit { guildConfigurations[guild.id]?.suggestionChannel = channel.id }
                }

                "review" -> {
                    configuration.edit { guildConfigurations[guild.id]?.suggestionReviewChannel = channel.id }
                }
            }
            respond("Set the **$option** channel to ${channel.mention}")
        }
    }

    sub("toggleShowVotes", "Toggle votes being displayed on in-progress suggestions.") {
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

    sub("toggleRemoveReactions", "Toggle reactions being removed on in-progress suggestions.") {
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

    sub("toggleVotingDM", "Toggle DMs being sent upon voting for a suggestions.") {
        execute {
            if (!configuration.hasGuildConfig(guild.id)) {
                respond("Please run the **configure** command to set this initially.")
                return@execute
            }
            val guildConfig = configuration[guild.id] ?: return@execute
            guildConfig.sendVotingDM = !guildConfig.sendVotingDM
            respond("Toggled sending DMs ${if (guildConfig.sendVotingDM) "**On**" else "**Off**"}")
        }
    }

    sub("view", "View guild configuration") {
        execute {
            val config = configuration[guild.id] ?: return@execute
            respond { createConfigurationEmbed(guild, config) }
        }
    }
}