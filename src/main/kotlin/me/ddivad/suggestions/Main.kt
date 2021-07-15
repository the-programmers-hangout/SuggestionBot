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
            addInlineField("Contributors", "[Link](https://github.com/the-programmers-hangout/JudgeBot/graphs/contributors)")

            val kotlinVersion = KotlinVersion.CURRENT
            val versions = it.discord.versions
            field {
                name = "Build Info"
                value = "```" +
                        "Version:   1.2.1\n" +
                        "DiscordKt: ${versions.library}\n" +
                        "Kotlin:    $kotlinVersion\n" +
                        "Kord:      ${versions.kord}\n" +
                        "```"
            }
            field {
                name = "Ping"
                value = botStats.ping
                inline = true
            }
            field {
                name = "Source"
                value = "[Github](https://github.com/the-programmers-hangout/SuggestionBot)"
                inline = true
            }
            field {
                name = "Uptime"
                value = botStats.uptime
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
