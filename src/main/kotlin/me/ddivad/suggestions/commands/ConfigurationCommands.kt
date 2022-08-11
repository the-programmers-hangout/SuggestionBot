package me.ddivad.suggestions.commands

import me.ddivad.suggestions.dataclasses.BotPermissions
import me.ddivad.suggestions.dataclasses.Configuration
import me.jakejmattson.discordkt.commands.commands

@Suppress("unused")
fun configurationCommands(configuration: Configuration) = commands("Configuration", BotPermissions.Admin) {
    text("toggleShowVotes") {
        description = "Toggle votes being displayed on in-progress suggestions."
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

    text("toggleRemoveReactions") {
        description = "Toggle reactions being removed on in-progress suggestions."
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

    text("toggleVotingDM") {
        description = "Toggle DMs being sent upon voting for a suggestions."
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
}