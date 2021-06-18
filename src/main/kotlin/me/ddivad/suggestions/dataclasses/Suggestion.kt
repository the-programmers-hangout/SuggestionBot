package me.ddivad.suggestions.dataclasses

enum class SuggestionStatus {
    NEW, POSTED, UNDER_REVIEW, IMPLEMENTED, REJECTED
}

data class Suggestion(
    val author: String,
    val suggestion: String,
    val upvotes: MutableList<String> = mutableListOf(),
    val downvotes: MutableList<String> = mutableListOf(),
    var status: SuggestionStatus = SuggestionStatus.NEW,
    var id: Int = 0,
    var publishedMessageId: String? = null,
    var reviewMessageId: String? = null
) {
    fun addReviewMessageId(messageId: String) {
        this.reviewMessageId = messageId
    }

    fun addPublishMessageId(messageId: String) {
        this.publishedMessageId = messageId
    }
}