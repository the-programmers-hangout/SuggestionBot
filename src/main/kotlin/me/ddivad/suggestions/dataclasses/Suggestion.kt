package me.ddivad.suggestions.dataclasses

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

@Serializable
enum class SuggestionStatus {
    NEW, PUBLISHED, UNDER_REVIEW, IMPLEMENTED, REJECTED
}

@Serializable
data class Suggestion(
    val author: Snowflake,
    val suggestion: String,
    var upvotes: MutableList<Snowflake> = mutableListOf(),
    var downvotes: MutableList<Snowflake> = mutableListOf(),
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

    fun reset() {
        this.upvotes = mutableListOf()
        this.downvotes = mutableListOf()
        this.publishedMessageId = null
        this.status = SuggestionStatus.NEW
    }
}