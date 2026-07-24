package net.thunderbird.feature.ai.api.repository

import kotlinx.coroutines.flow.StateFlow
import net.thunderbird.core.outcome.Outcome
import net.thunderbird.feature.ai.api.domain.model.AiConfig
import net.thunderbird.feature.ai.api.domain.model.AiError
import net.thunderbird.feature.ai.api.domain.model.AiThreatAnalysis
import net.thunderbird.feature.ai.api.domain.model.AiWritingSuggestion

interface AiConfigRepository {
    val aiConfig: StateFlow<AiConfig>
    suspend fun saveAiConfig(config: AiConfig)
}

interface AnalyzeEmailThreatUseCase {
    suspend operator fun invoke(
        subject: String,
        sender: String,
        bodyText: String,
    ): Outcome<AiThreatAnalysis, AiError>
}

interface GenerateWritingSuggestionUseCase {
    suspend operator fun invoke(
        subject: String,
        sender: String,
        incomingBody: String,
        promptOrTone: String,
    ): Outcome<AiWritingSuggestion, AiError>
}

interface ContinueWritingUseCase {
    suspend operator fun invoke(
        subject: String,
        sender: String,
        incomingBody: String,
        currentDraft: String,
    ): Outcome<AiWritingSuggestion, AiError>
}
