package me.ddivad.suggestions.util

import me.ddivad.suggestions.dataclasses.Suggestion
import me.ddivad.suggestions.embeds.createSuggestionReviewEmbed

data class Vote(
    val opinion: Int,
    val upVotes: Int,
    val downVotes: Int,
    val upvotePercentage: Double,
    val downVotePercentage: Double
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