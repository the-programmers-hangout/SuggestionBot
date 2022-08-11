package me.ddivad.suggestions.services

import dev.kord.core.entity.Guild
import me.ddivad.suggestions.dataclasses.Configuration
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.discordkt.extensions.toTimeString
import java.util.*

@Service
class BotStatsService(private val configuration: Configuration, private val discord: Discord) {
    private var startTime: Date = Date()

    val uptime: String
        get() = ((Date().time - startTime.time) / 1000).toTimeString()

    val ping: String
        get() = "${discord.kord.gateway.averagePing}"

    fun upvoteAdded(guild: Guild) {
        with(configuration) {
            configuration[guild.id]?.let {
                it.statistics.totalUpvotes++
            }
            statistics.totalUpvotes++
            save()
        }
    }

    fun downvoteAdded(guild: Guild) {
        with(configuration) {
            configuration[guild.id]?.let {
                it.statistics.totalDownvotes++
            }
            statistics.totalDownvotes++
            save()
        }
    }

    fun suggestionAdded(guild: Guild) {
        with(configuration) {
            configuration[guild.id]?.let {
                it.statistics.totalSuggestions++
            }
            statistics.totalSuggestions++
            save()
        }
    }
}