package me.ddivad.suggestions.dataclasses

import dev.kord.common.entity.Snowflake

enum class SuggestionStatus {
    NEW, POSTED, UNDER_REVIEW, IMPLEMENTED, REJECTED
}

data class Suggestion(
    val author: Snowflake,
    val suggestion: String,
    val upvotes: MutableList<Snowflake> = mutableListOf(),
    val downvotes: MutableList<Snowflake> = mutableListOf(),
    var status: SuggestionStatus = SuggestionStatus.NEW,
    var id: Int = 0,
    var publishedMessageId: Snowflake? = null,
    var reviewMessageId: Snowflake? = null
) {
    fun addReviewMessageId(messageId: Snowflake) {
        this.reviewMessageId = messageId
    }

    fun addPublishMessageId(messageId: Snowflake) {
        this.publishedMessageId = messageId
    }
}