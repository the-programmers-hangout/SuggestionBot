package me.ddivad.suggestions

import com.gitlab.kordlib.gateway.Intent
import com.gitlab.kordlib.gateway.Intents
import com.gitlab.kordlib.gateway.PrivilegedIntent
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.services.BotStatsService
import me.ddivad.suggestions.services.PermissionLevel
import me.ddivad.suggestions.services.PermissionsService
import me.ddivad.suggestions.services.requiredPermissionLevel
import me.jakejmattson.discordkt.api.dsl.bot
import me.jakejmattson.discordkt.api.extensions.addInlineField
import java.awt.Color

@PrivilegedIntent
suspend fun main() {
    val token = System.getenv("BOT_TOKEN") ?: null
    val prefix = System.getenv("DEFAULT_PREFIX") ?: "s!"

    require(token != null) { "Expected the bot token as an environment variable" }

    bot(token) {
        prefix {
            val configuration = discord.getInjectionObjects(Configuration::class)
            guild?.let { configuration[it.id.longValue]?.prefix } ?: prefix
        }

        configure {
            allowMentionPrefix = true
            commandReaction = null
            theme = Color.MAGENTA
        }

        mentionEmbed {
            val botStats = it.discord.getInjectionObjects(BotStatsService::class)
            val channel = it.channel
            val self = channel.kord.getSelf()

            color = it.discord.configuration.theme

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
                        "Version:   1.0.0\n" +
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

        permissions {
            if (guild != null) {
                val member = user.asMember(guild!!.id)
                val permission = command.requiredPermissionLevel
                val permissionsService = discord.getInjectionObjects(PermissionsService::class)
                return@permissions permissionsService.hasClearance(guild, member, permission)
            } else return@permissions command.requiredPermissionLevel == PermissionLevel.Everyone
        }

        intents {
            Intents.nonPrivileged.intents.forEach {
                +it
            }
            +Intent.GuildMembers
        }

        presence {
            playing("s!suggest")
        }
    }
}