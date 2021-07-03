package me.ddivad.suggestions.dataclasses

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.dsl.Data

data class Configuration(
    val ownerId: Snowflake? = null,
    var prefix: String = "++",
    val guildConfigurations: MutableMap<Long, GuildConfiguration> = mutableMapOf()
) : Data("config/config.json") {
    fun hasGuildConfig(guildId: Snowflake) = guildConfigurations.containsKey(guildId.value)
    operator fun get(id: Snowflake) = guildConfigurations[id.value]
    fun setup(
        guild: Guild,
        prefix: String,
        adminRole: Role,
        staffRole: Role,
        suggestionChannel: TextChannel,
        suggestionReviewChannel: TextChannel
    ) {
        if (guildConfigurations[guild.id.value] != null) return

        val newConfiguration = GuildConfiguration(
            guild.id.value,
            prefix,
            staffRole.id,
            adminRole.id,
            suggestionChannel.id,
            suggestionReviewChannel.id
        )
        guildConfigurations[guild.id.value] = newConfiguration
        save()
    }
}

data class GuildConfiguration(
    val id: Long? = null,
    var prefix: String = "s!",
    var staffRoleId: Snowflake,
    var adminRoleId: Snowflake,
    var suggestionChannel: Snowflake,
    var suggestionReviewChannel: Snowflake,
    val suggestions: MutableList<Suggestion> = mutableListOf(),
)