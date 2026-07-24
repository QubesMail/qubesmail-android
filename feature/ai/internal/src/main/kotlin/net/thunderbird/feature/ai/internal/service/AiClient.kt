package net.thunderbird.feature.ai.internal.service

import net.thunderbird.core.outcome.Outcome
import net.thunderbird.feature.ai.api.domain.model.AiConfig
import net.thunderbird.feature.ai.api.domain.model.AiError

internal interface AiClient {
    suspend fun executeCompletion(
        config: AiConfig,
        systemInstruction: String,
        userPrompt: String,
    ): Outcome<String, AiError>
}
