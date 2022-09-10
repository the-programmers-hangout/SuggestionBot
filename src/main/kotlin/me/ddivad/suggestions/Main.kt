package me.ddivad.suggestions

import dev.kord.common.annotation.KordPreview
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import me.ddivad.suggestions.dataclasses.BotPermissions
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.services.InteractionUpdateService
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.extensions.plus
import java.awt.Color

@KordPreview
@PrivilegedIntent
suspend fun main() {
    val token = System.getenv("BOT_TOKEN") ?: null

    bot(token) {
        data("config/config.json") { Configuration() }

        prefix { "/" }

        configure {
            commandReaction = null
            recommendCommands = false
            theme = Color.MAGENTA
            intents = Intent.DirectMessagesReactions + Intent.GuildMessageReactions
            defaultPermissions = BotPermissions.Everyone
        }

        presence {
            watching("suggestions")
        }

        onStart {
            val interactionUpdateService = this.getInjectionObjects(InteractionUpdateService::class)
            try {
                interactionUpdateService.run()
            } catch (ex: Exception) {
                println(ex.message)
            }
        }
    }
}
