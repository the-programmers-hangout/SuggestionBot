package me.ddivad.suggestions.dataclasses

import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.ReactionEmoji
import com.gitlab.kordlib.core.entity.Role
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.kordx.emoji.Emojis
import com.gitlab.kordlib.rest.route.Route
import me.jakejmattson.discordkt.api.dsl.Data

data class Configuration(
    val ownerId: String = "insert-owner-id",
    var prefix: String = "++",
    val guildConfigurations: MutableMap<Long, GuildConfiguration> = mutableMapOf()
) : Data("config/config.json") {
    operator fun get(id: Long) = guildConfigurations[id]
    fun hasGuildConfig(guildId: Long) = guildConfigurations.containsKey(guildId)

    fun setup(
        guild: Guild,
        prefix: String,
        adminRole: Role,
        staffRole: Role,
        suggestionChannel: TextChannel,
        suggestionReviewChannel: TextChannel
    ) {
        if (guildConfigurations[guild.id.longValue] != null) return

        val newConfiguration = GuildConfiguration(
            guild.id.value,
            prefix,
            staffRole.id.value,
            adminRole.id.value,
            suggestionChannel.id.value,
            suggestionReviewChannel.id.value
        )
        guildConfigurations[guild.id.longValue] = newConfiguration
        save()
    }
}

data class GuildConfiguration(
    val id: String = "",
    var prefix: String = "s!",
    var staffRoleId: String,
    var adminRoleId: String,
    var suggestionChannel: String,
    var suggestionReviewChannel: String,
    val suggestions: MutableList<Suggestion> = mutableListOf(),
)