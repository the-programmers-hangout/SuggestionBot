package me.ddivad.suggestions

import dev.kord.common.annotation.KordPreview
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import me.ddivad.suggestions.dataclasses.BotPermissions
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.services.CacheService
import me.ddivad.suggestions.services.InteractionUpdateService
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.extensions.plus
import java.awt.Color

@KordPreview
@PrivilegedIntent
suspend fun main() {
    val token = System.getenv("BOT_TOKEN") ?: null

    bot(token) {
        val configuration = data("config/config.json") { Configuration() }

        prefix {
            configuration.prefix
        }

        configure {
            commandReaction = null
            theme = Color.MAGENTA
            entitySupplyStrategy = EntitySupplyStrategy.cacheWithCachingRestFallback
            intents = Intent.DirectMessagesReactions + Intent.GuildMessageReactions
            defaultPermissions = BotPermissions.Everyone
        }

        presence {
            playing("s!suggest")
        }

        onStart {
            val (cacheService, interactionUpdateService) = this.getInjectionObjects(
                CacheService::class,
                InteractionUpdateService::class
            )
            try {
                cacheService.run()
                interactionUpdateService.run()
            } catch (ex: Exception) {
                println(ex.message)
            }
        }
    }
}
