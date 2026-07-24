package net.thunderbird.feature.ai.internal.usecase

import net.thunderbird.core.logging.Logger
import net.thunderbird.core.outcome.Outcome
import net.thunderbird.core.outcome.flatMapSuccess
import net.thunderbird.feature.ai.api.domain.model.AiError
import net.thunderbird.feature.ai.api.domain.model.AiThreatAnalysis
import net.thunderbird.feature.ai.api.domain.model.ThreatLevel
import net.thunderbird.feature.ai.api.repository.AiConfigRepository
import net.thunderbird.feature.ai.api.repository.AnalyzeEmailThreatUseCase
import net.thunderbird.feature.ai.internal.service.AiClientProvider
import net.thunderbird.feature.ai.internal.service.AiPromptBuilder
import net.thunderbird.feature.ai.internal.service.AiResponseParser

private const val TAG = "AnalyzeEmailThreat"

internal class DefaultAnalyzeEmailThreatUseCase(
    private val configRepository: AiConfigRepository,
    private val clientProvider: AiClientProvider,
    private val logger: Logger,
) : AnalyzeEmailThreatUseCase {

    override suspend fun invoke(
        subject: String,
        sender: String,
        bodyText: String,
    ): Outcome<AiThreatAnalysis, AiError> {
        val config = configRepository.aiConfig.value

        if (!config.enabled || !config.enableThreatDetection) {
            return Outcome.success(
                AiThreatAnalysis(
                    level = ThreatLevel.SAFE,
                    summary = "AI threat detection is disabled.",
                ),
            )
        }

        if (config.apiKey.isBlank()) {
            logger.warn(TAG) { "AI threat analysis skipped: API key not configured" }
            return Outcome.failure(AiError.MissingApiKey)
        }

        logger.debug(TAG) { "Starting threat analysis for email" }

        val client = clientProvider.createClient(config.providerType)
        val prompt = AiPromptBuilder.buildThreatAnalysisPrompt(subject, sender, bodyText)

        return client.executeCompletion(
            config = config,
            systemInstruction = AiPromptBuilder.THREAT_SYSTEM_INSTRUCTION,
            userPrompt = prompt,
        ).flatMapSuccess { rawContent ->
            AiResponseParser.parseThreatAnalysis(rawContent)
        }.also { result ->
            when (result) {
                is Outcome.Success -> logger.debug(TAG) { "Threat analysis complete: ${result.data.level}" }
                is Outcome.Failure -> logger.warn(TAG) { "Threat analysis failed: ${result.error}" }
            }
        }
    }
}
