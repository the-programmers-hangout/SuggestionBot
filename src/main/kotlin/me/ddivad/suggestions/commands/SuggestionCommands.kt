package me.ddivad.suggestions.commands

import dev.kord.common.annotation.KordPreview
import me.ddivad.suggestions.dataclasses.BotPermissions
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.ddivad.suggestions.embeds.createStatsEmbed
import me.ddivad.suggestions.services.SuggestionService
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.arguments.IntegerArg
import me.jakejmattson.discordkt.commands.commands

@KordPreview
@Suppress("unused")
fun suggestionCommands(configuration: Configuration, suggestionService: SuggestionService) = commands("Suggestions") {
    slash("suggest", "Make a suggestion.", BotPermissions.Everyone) {
        execute(EveryArg("Suggestion")) {
            val guildConfiguration = configuration[guild.id] ?: return@execute

            if (guildConfiguration.requiredSuggestionRole == null || author.asMember(guild.id).roleIds.contains(
                    guildConfiguration.requiredSuggestionRole
                )
            ) {
                val nextId: Int =
                    if (guildConfiguration.suggestions.isEmpty()) 1 else guildConfiguration.suggestions.maxByOrNull { it.id }!!.id + 1
                val suggestion = Suggestion(author.id, args.first, id = nextId)
                suggestionService.addSuggestion(guild, suggestion)
                respond(
                    "Suggestion added to the pool. If accepted, it will appear in ${
                        guild.getChannel(
                            guildConfiguration.suggestionChannel
                        ).mention
                    }"
                )
            } else respond("Sorry, you don't meet the role requirements to post a suggestion.")
        }
    }

    slash("setStatus", "Set the status for a suggestion (backup for interaction buttons)", BotPermissions.Admin) {
        execute(
            IntegerArg("ID"),
            ChoiceArg("Status", "The status of this suggestion", "accepted", "rejected", "review", "implemented")
        ) {
            val (id, status) = args
            val suggestion = suggestionService.findSuggestionById(guild, id)

            if (suggestion != null) {
                when (status.lowercase()) {
                    "accepted" -> suggestionService.updateStatus(guild, suggestion, SuggestionStatus.PUBLISHED)
                    "review" -> suggestionService.updateStatus(guild, suggestion, SuggestionStatus.UNDER_REVIEW)
                    "implemented" -> suggestionService.updateStatus(guild, suggestion, SuggestionStatus.IMPLEMENTED)
                    "rejected" -> suggestionService.updateStatus(guild, suggestion, SuggestionStatus.REJECTED)
                }
            }
        }
    }

    slash("stats", "Get stats about guild suggestions", BotPermissions.Staff) {
        execute {
            respondPublic {
                createStatsEmbed(guild, configuration)
            }
        }
    }
}