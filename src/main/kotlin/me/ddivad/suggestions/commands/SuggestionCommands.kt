package me.ddivad.suggestions.commands

import kotlinx.coroutines.flow.toList
import me.ddivad.suggestions.conversations.guildChoiceConversation
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.Permissions
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.ddivad.suggestions.services.SuggestionService
import me.jakejmattson.discordkt.api.arguments.ChoiceArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.arguments.IntegerArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.extensions.mutualGuilds

@Suppress("unused")
fun suggestionCommands(configuration: Configuration, suggestionService: SuggestionService) = commands("Suggestions") {
    command("suggest") {
        description = "Make a suggestion."
        requiredPermission = Permissions.NONE
        execute(EveryArg("Suggestion")) {
            if (guild != null) {
                val guildConfiguration = configuration[guild!!.id] ?: return@execute
                val nextId: Int =
                    if (guildConfiguration.suggestions.isEmpty()) 1 else guildConfiguration.suggestions.maxByOrNull { it.id }!!.id + 1
                val suggestion = Suggestion(author.id, args.first, id = nextId)
                suggestionService.addSuggestion(guild!!, suggestion)
            } else {
                val mutualGuilds = author.mutualGuilds.toList().filter { configuration[it.id] != null }
                when {
                    mutualGuilds.size > 1 -> {
                        guildChoiceConversation(mutualGuilds, args.first, suggestionService, configuration)
                            .startPrivately(discord, author)
                    }
                    else -> {
                        val guild = mutualGuilds.firstOrNull() ?: return@execute
                        val guildConfiguration = configuration[guild.id] ?: return@execute
                        val nextId: Int =
                            if (guildConfiguration.suggestions.isEmpty()) 1 else guildConfiguration.suggestions.maxByOrNull { it.id }!!.id + 1
                        val suggestion = Suggestion(author.id, args.first, id = nextId)
                        suggestionService.addSuggestion(guild, suggestion)
                    }
                }
            }
        }
    }

    guildCommand("setStatus") {
        description = "Set the status for a suggestion (backup for interaction buttons)"
        requiredPermission = Permissions.ADMINISTRATOR
        execute(IntegerArg("Suggestion ID"), ChoiceArg("Status", "accepted",  "rejected", "review", "implemented")) {
            val (id, status) = args
            val suggestion = suggestionService.findSuggestionById(guild, id)
            if (suggestion != null) {
                when (status.toLowerCase()) {
                    "accepted" -> {
                        suggestionService.updateStatus(
                            guild,
                            suggestion,
                            SuggestionStatus.POSTED
                        )
                    }
                    "review" -> {
                        suggestionService.updateStatus(
                            guild,
                            suggestion,
                            SuggestionStatus.UNDER_REVIEW
                        )
                    }
                    "implemented" -> {
                        suggestionService.updateStatus(
                            guild,
                            suggestion,
                            SuggestionStatus.IMPLEMENTED
                        )
                    }
                    "rejected" -> {
                        suggestionService.updateStatus(
                            guild,
                            suggestion,
                            SuggestionStatus.REJECTED
                        )
                    }
                }
            }
        }
    }
}