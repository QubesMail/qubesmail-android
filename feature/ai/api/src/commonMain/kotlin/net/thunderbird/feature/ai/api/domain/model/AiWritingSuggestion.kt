package net.thunderbird.feature.ai.api.domain.model

data class AiWritingSuggestion(
    val replyText: String,
    val suggestedTone: String = "Professional",
    val bulletPoints: List<String> = emptyList(),
)
