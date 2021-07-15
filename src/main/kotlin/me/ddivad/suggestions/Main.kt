package me.ddivad.suggestions

import dev.kord.common.kColor
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.PrivilegedIntent
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.Permissions
import me.ddivad.suggestions.services.*
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.addInlineField
import java.awt.Color

@PrivilegedIntent
suspend fun main() {
    val token = System.getenv("BOT_TOKEN") ?: null

    require(token != null) { "Expected the bot token as an environment variable" }

    bot(token) {
        prefix {
            val configuration = discord.getInjectionObjects(Configuration::class)
            configuration.prefix
        }

        configure {
            allowMentionPrefix = true
            commandReaction = null
            theme = Color.MAGENTA
            entitySupplyStrategy = EntitySupplyStrategy.cacheWithCachingRestFallback
            permissions(Permissions.NONE)
        }

        mentionEmbed {
            val botStats = it.discord.getInjectionObjects(BotStatsService::class)
            val channel = it.channel
            val self = channel.kord.getSelf()

            color = it.discord.configuration.theme?.kColor

            thumbnail {
                url = self.avatar.url
            }

            field {
                name = self.tag
                value = "A bot to manage suggestions for a guild."
            }

            addInlineField("Prefix", "`${it.prefix()}`")
            addInlineField("Contributors", "ddivad#0001")

            val kotlinVersion = KotlinVersion.CURRENT
            val versions = it.discord.versions
            field {
                name = "Build Info"
                value = "```" +
                        "Version:   1.1.1\n" +
                        "DiscordKt: ${versions.library}\n" +
                        "Kotlin:    $kotlinVersion" +
                        "```"
            }

            field {
                name = "Uptime"
                value = botStats.uptime
            }
            field {
                name = "Ping"
                value = botStats.ping
            }
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
