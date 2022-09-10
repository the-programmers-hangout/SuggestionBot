package me.ddivad.suggestions.services

import dev.kord.common.annotation.KordPreview
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.SuggestionStatus
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service

@KordPreview
@Service
class InteractionUpdateService(
    private val discord: Discord,
    private val configuration: Configuration,
    private val suggestionService: SuggestionService
) {
    suspend fun run() {
        configuration.guildConfigurations.forEach { config ->
            val guild = discord.kord.getGuild(config.key) ?: return@forEach
            val guildConfig = configuration[guild.id] ?: return@forEach

            guildConfig.suggestions.forEach {
                if (it.status in setOf(
                        SuggestionStatus.NEW,
                        SuggestionStatus.PUBLISHED,
                        SuggestionStatus.UNDER_REVIEW
                    )
                ) {
                    it.reviewMessageId = suggestionService.resetSuggestionInteractions(guild, it)
                }
            }
            configuration.save()
        }
    }
}