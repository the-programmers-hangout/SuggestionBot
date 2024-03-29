package me.ddivad.suggestions.dataclasses

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.TextChannel
import kotlinx.serialization.Serializable
import me.jakejmattson.discordkt.dsl.Data
import me.jakejmattson.discordkt.dsl.edit

@Serializable
data class Configuration(
    val statistics: Statistics = Statistics(),
    val guildConfigurations: MutableMap<Snowflake, GuildConfiguration> = mutableMapOf()
) : Data() {
    fun hasGuildConfig(guildId: Snowflake) = guildConfigurations.containsKey(guildId)
    operator fun get(id: Snowflake) = guildConfigurations[id]

    fun setup(
        guild: Guild,
        suggestionChannel: TextChannel,
        suggestionReviewChannel: TextChannel,
        showVotes: Boolean,
        removeReactions: Boolean,
        sendVotingDM: Boolean
    ): GuildConfiguration? {
        if (guildConfigurations[guild.id] != null) return guildConfigurations[guild.id]

        val newConfiguration = GuildConfiguration(
            guild.id,
            null,
            suggestionChannel.id,
            suggestionReviewChannel.id,
            showVotes,
            removeReactions,
            sendVotingDM
        )
        edit {
            guildConfigurations[guild.id] = newConfiguration
        }
        return newConfiguration
    }
}

@Serializable
data class GuildConfiguration(
    val id: Snowflake? = null,
    var requiredSuggestionRole: Snowflake?,
    var suggestionChannel: Snowflake,
    var suggestionReviewChannel: Snowflake,
    var showVotes: Boolean,
    var removeVoteReactions: Boolean,
    var sendVotingDM: Boolean,
    val suggestions: MutableList<Suggestion> = mutableListOf(),
    val statistics: Statistics = Statistics()
)

@Serializable
data class Statistics(
    var totalSuggestions: Int = 0,
    var totalUpvotes: Int = 0,
    var totalDownvotes: Int = 0
)