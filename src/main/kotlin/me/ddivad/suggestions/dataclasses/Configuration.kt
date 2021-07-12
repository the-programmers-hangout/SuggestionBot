package me.ddivad.suggestions.dataclasses

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Role
import dev.kord.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.dsl.Data

data class Configuration(
    val ownerId: String = "insert id here",
    var prefix: String = "s!",
    val guildConfigurations: MutableMap<Long, GuildConfiguration> = mutableMapOf()
) : Data("config/config.json") {
    fun hasGuildConfig(guildId: Snowflake) = guildConfigurations.containsKey(guildId.value)
    operator fun get(id: Snowflake) = guildConfigurations[id.value]
    fun setup(
        guild: Guild,
        adminRole: Role,
        staffRole: Role,
        suggestionChannel: TextChannel,
        suggestionReviewChannel: TextChannel,
        showVotes: Boolean,
        removeReactions: Boolean,
        sendVotingDM: Boolean
    ) {
        if (guildConfigurations[guild.id.value] != null) return

        val newConfiguration = GuildConfiguration(
            guild.id.value,
            staffRole.id,
            adminRole.id,
            suggestionChannel.id,
            suggestionReviewChannel.id,
            showVotes,
            removeReactions,
            sendVotingDM
        )
        guildConfigurations[guild.id.value] = newConfiguration
        save()
    }
}

data class GuildConfiguration(
    val id: Long? = null,
    var staffRoleId: Snowflake,
    var adminRoleId: Snowflake,
    var suggestionChannel: Snowflake,
    var suggestionReviewChannel: Snowflake,
    var showVotes: Boolean,
    var removeVoteReactions: Boolean,
    var sendVotingDM: Boolean,
    val suggestions: MutableList<Suggestion> = mutableListOf(),
)