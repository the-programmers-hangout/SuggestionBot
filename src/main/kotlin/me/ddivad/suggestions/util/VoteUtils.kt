package me.ddivad.suggestions.util

import me.ddivad.suggestions.dataclasses.Suggestion

data class Vote(
    val opinion: Int,
    val upVotes: Int,
    val downVotes: Int,
    val upvotePercentage: Int,
    val downVotePercentage: Int
)

fun getVoteCounts(suggestion: Suggestion): Vote {
    val upVotes = suggestion.upvotes.size
    val downVotes = suggestion.downvotes.size
    val totalVotes = upVotes + downVotes

    val upVotePercentage = if(totalVotes > 0) upVotes / totalVotes * 100 else 0
    val downVotePercentage = if(totalVotes > 0) downVotes / totalVotes * 100 else 0

    return Vote(
        upVotes - downVotes,
        upVotes,
        downVotes,
        upVotePercentage,
        downVotePercentage
    )
}