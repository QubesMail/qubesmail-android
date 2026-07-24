package net.thunderbird.feature.ai.internal.service

import io.ktor.client.HttpClient
import net.thunderbird.feature.ai.api.domain.model.AiProviderType

internal fun interface AiClientProvider {
    fun createClient(providerType: AiProviderType): AiClient
}

internal class DefaultAiClientProvider(
    private val httpClient: HttpClient,
) : AiClientProvider {
    override fun createClient(providerType: AiProviderType): AiClient {
        return when (providerType) {
            AiProviderType.CHAT_GPT,
            AiProviderType.CUSTOM_REMOTE -> OpenAiClient(httpClient)
            AiProviderType.GEMINI -> GeminiClient(httpClient)
            AiProviderType.CLAUDE -> ClaudeClient(httpClient)
        }
    }
}
