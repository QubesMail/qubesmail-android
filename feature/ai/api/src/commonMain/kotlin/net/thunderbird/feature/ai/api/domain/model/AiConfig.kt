package net.thunderbird.feature.ai.api.domain.model

data class AiConfig(
    val enabled: Boolean = false,
    val providerType: AiProviderType = AiProviderType.CHAT_GPT,
    val endpointUrl: String = AiProviderType.CHAT_GPT.defaultEndpointUrl,
    val apiKey: String = "",
    val modelName: String = AiProviderType.CHAT_GPT.defaultModelName,
    val enableThreatDetection: Boolean = true,
    val enableWritingAssistant: Boolean = true,
)
