package me.ddivad.suggestions.util

import dev.kord.core.entity.Guild
import me.ddivad.suggestions.dataclasses.Configuration
import me.ddivad.suggestions.dataclasses.Suggestion

data class Vote(
    val opinion: Int,
    val upVotes: Int,
    val downVotes: Int,
    val upvotePercentage: Double,
    val downVotePercentage: Double
)

data class Totals(
    val suggestions: Int,
    val votes: Int,
    val upVotes: Int,
    val downVotes: Int,
    val upvotePercentage: Double,
    val downVotePercentage: Double
)

data class CombinedStats(
    val overall: Totals,
    val guild: Totals
)

fun getVoteCounts(suggestion: Suggestion): Vote {
    val upVotes = suggestion.upvotes.size
    val downVotes = suggestion.downvotes.size
    val totalVotes = upVotes + downVotes

    val upVotePercentage = if(totalVotes > 0) upVotes.toDouble() / totalVotes.toDouble() * 100 else 0.0
    val downVotePercentage = if(totalVotes > 0) downVotes.toDouble() / totalVotes.toDouble() * 100 else 0.0

    return Vote(
        upVotes - downVotes,
        upVotes,
        downVotes,
        "%.${2}f".format(upVotePercentage).toDouble(),
        "%.${2}f".format(downVotePercentage).toDouble()
    )
}

fun getTotalCounts(guild: Guild, configuration: Configuration): CombinedStats? {
    val guildStats = configuration[guild.id]?.statistics ?: return null
    val totalGuildVotes = guildStats.totalDownvotes + guildStats.totalUpvotes
    val totalOverallVotes = configuration.statistics.totalUpvotes + configuration.statistics.totalDownvotes
    val guildTotals = Totals(
        guildStats.totalSuggestions,
        totalGuildVotes,
        guildStats.totalUpvotes,
        guildStats.totalDownvotes,
        "%.${2}f".format(guildStats.totalUpvotes.toDouble() / totalGuildVotes).toDouble() * 100,
        "%.${2}f".format(guildStats.totalDownvotes.toDouble() / totalGuildVotes).toDouble() * 100,
    )
    val overallTotals = Totals(
        configuration.statistics.totalSuggestions,
        totalOverallVotes,
        configuration.statistics.totalUpvotes,
        configuration.statistics.totalDownvotes,
        "%.${2}f".format(configuration.statistics.totalUpvotes.toDouble() / totalOverallVotes).toDouble() * 100,
        "%.${2}f".format(configuration.statistics.totalDownvotes.toDouble() / totalOverallVotes).toDouble() * 100
    )

    return CombinedStats(
        overallTotals,
        guildTotals
    )
}