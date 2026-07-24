package net.thunderbird.feature.ai.internal.usecase

import net.thunderbird.core.logging.Logger
import net.thunderbird.core.outcome.Outcome
import net.thunderbird.core.outcome.flatMapSuccess
import net.thunderbird.feature.ai.api.domain.model.AiError
import net.thunderbird.feature.ai.api.domain.model.AiWritingSuggestion
import net.thunderbird.feature.ai.api.repository.AiConfigRepository
import net.thunderbird.feature.ai.api.repository.ContinueWritingUseCase
import net.thunderbird.feature.ai.internal.service.AiClientProvider
import net.thunderbird.feature.ai.internal.service.AiPromptBuilder
import net.thunderbird.feature.ai.internal.service.AiResponseParser

private const val TAG = "ContinueWriting"

internal class DefaultContinueWritingUseCase(
    private val configRepository: AiConfigRepository,
    private val clientProvider: AiClientProvider,
    private val logger: Logger,
) : ContinueWritingUseCase {

    override suspend fun invoke(
        subject: String,
        sender: String,
        incomingBody: String,
        currentDraft: String,
    ): Outcome<AiWritingSuggestion, AiError> {
        val config = configRepository.aiConfig.value

        if (!config.enabled || !config.enableWritingAssistant) {
            return Outcome.failure(AiError.Disabled)
        }

        if (config.apiKey.isBlank()) {
            logger.warn(TAG) { "AI continue writing skipped: API key not configured" }
            return Outcome.failure(AiError.MissingApiKey)
        }

        logger.debug(TAG) { "Generating writing continuation" }

        val client = clientProvider.createClient(config.providerType)
        val prompt = AiPromptBuilder.buildContinueWritingPrompt(subject, sender, incomingBody, currentDraft)

        return client.executeCompletion(
            config = config,
            systemInstruction = AiPromptBuilder.WRITING_SYSTEM_INSTRUCTION,
            userPrompt = prompt,
        ).flatMapSuccess { rawContent ->
            AiResponseParser.parseWritingSuggestion(rawContent)
        }.also { result ->
            when (result) {
                is Outcome.Success -> logger.debug(TAG) { "Writing continuation generated" }
                is Outcome.Failure -> logger.warn(TAG) { "Writing continuation failed: ${result.error}" }
            }
        }
    }
}
