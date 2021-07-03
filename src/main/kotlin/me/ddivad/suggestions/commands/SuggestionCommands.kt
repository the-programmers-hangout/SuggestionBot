package me.ddivad.suggestions.commands

import kotlinx.coroutines.flow.toList
import me.ddivad.suggestions.conversations.guildChoiceConversation
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.ddivad.suggestions.services.PermissionLevel
import me.ddivad.suggestions.services.SuggestionService
import me.ddivad.suggestions.services.requiredPermissionLevel
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.arguments.IntegerArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.extensions.mutualGuilds

@Suppress("unused")
fun suggestionCommands(configuration: Configuration, suggestionService: SuggestionService) = commands("Suggestions") {
    command("suggest") {
        description = "Make a suggestion."
        requiredPermissionLevel = PermissionLevel.Everyone
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

    guildCommand("progress") {
        execute(IntegerArg("Suggestion Id")) {
            val suggestion = suggestionService.findSuggestionById(guild, args.first)
            if (suggestion != null) {
                when (suggestion.status) {
                    SuggestionStatus.NEW -> suggestionService.updateStatus(guild, suggestion, SuggestionStatus.POSTED)
                    SuggestionStatus.POSTED -> suggestionService.updateStatus(
                        guild,
                        suggestion,
                        SuggestionStatus.UNDER_REVIEW
                    )
                    SuggestionStatus.UNDER_REVIEW -> suggestionService.updateStatus(
                        guild,
                        suggestion,
                        SuggestionStatus.IMPLEMENTED
                    )
                    else -> suggestionService.updateStatus(guild, suggestion, SuggestionStatus.NEW)
                }
            }
        }
    }
}