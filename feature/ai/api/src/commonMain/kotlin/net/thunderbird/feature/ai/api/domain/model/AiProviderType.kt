package net.thunderbird.feature.ai.api.domain.model

enum class AiProviderType(
    val displayName: String,
    val defaultEndpointUrl: String,
    val defaultModelName: String,
) {
    CHAT_GPT(
        displayName = "ChatGPT (OpenAI)",
        defaultEndpointUrl = "https://api.openai.com/v1",
        defaultModelName = "gpt-4o-mini",
    ),
    GEMINI(
        displayName = "Google Gemini",
        defaultEndpointUrl = "https://generativelanguage.googleapis.com",
        defaultModelName = "gemini-1.5-flash",
    ),
    CLAUDE(
        displayName = "Anthropic Claude",
        defaultEndpointUrl = "https://api.anthropic.com/v1",
        defaultModelName = "claude-3-5-sonnet-20241022",
    ),
    CUSTOM_REMOTE(
        displayName = "Custom Remote API",
        defaultEndpointUrl = "https://api.openai.com/v1",
        defaultModelName = "gpt-4o-mini",
    ),
}
