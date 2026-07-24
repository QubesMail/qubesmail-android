package net.thunderbird.feature.ai.internal.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.thunderbird.core.logging.Logger
import net.thunderbird.core.preference.storage.Storage
import net.thunderbird.core.preference.storage.StorageEditor
import net.thunderbird.core.preference.storage.getEnumOrDefault
import net.thunderbird.feature.ai.api.domain.model.AiConfig
import net.thunderbird.feature.ai.api.domain.model.AiProviderType
import net.thunderbird.feature.ai.api.repository.AiConfigRepository

private const val TAG = "AiConfigRepository"

internal class DefaultAiConfigRepository(
    private val storage: Storage,
    private val storageEditor: StorageEditor,
    private val logger: Logger,
) : AiConfigRepository {

    private val mutex = Mutex()
    private val _aiConfig = MutableStateFlow(loadConfig())
    override val aiConfig: StateFlow<AiConfig> = _aiConfig.asStateFlow()

    private fun loadConfig(): AiConfig {
        val providerType = try {
            storage.getEnumOrDefault(KEY_PROVIDER_TYPE, AiProviderType.CHAT_GPT)
        } catch (e: IllegalArgumentException) {
            logger.warn(TAG, e) { "Invalid provider type in storage, defaulting to CHAT_GPT" }
            AiProviderType.CHAT_GPT
        }

        return AiConfig(
            enabled = storage.getBoolean(KEY_ENABLED, false),
            providerType = providerType,
            endpointUrl = storage.getStringOrDefault(KEY_ENDPOINT_URL, providerType.defaultEndpointUrl),
            apiKey = storage.getStringOrDefault(KEY_API_KEY, ""),
            modelName = storage.getStringOrDefault(KEY_MODEL_NAME, providerType.defaultModelName),
            enableThreatDetection = storage.getBoolean(KEY_THREAT_DETECTION, true),
            enableWritingAssistant = storage.getBoolean(KEY_WRITING_ASSISTANT, true),
        )
    }

    override suspend fun saveAiConfig(config: AiConfig) {
        mutex.withLock {
            storageEditor
                .putBoolean(KEY_ENABLED, config.enabled)
                .putString(KEY_PROVIDER_TYPE, config.providerType.name)
                .putString(KEY_ENDPOINT_URL, config.endpointUrl)
                .putString(KEY_API_KEY, config.apiKey)
                .putString(KEY_MODEL_NAME, config.modelName)
                .putBoolean(KEY_THREAT_DETECTION, config.enableThreatDetection)
                .putBoolean(KEY_WRITING_ASSISTANT, config.enableWritingAssistant)
                .commit()

            _aiConfig.value = config
            logger.debug(TAG) { "AI config saved: provider=${config.providerType}" }
        }
    }

    private companion object {
        const val KEY_ENABLED = "ai_feature_enabled"
        const val KEY_PROVIDER_TYPE = "ai_provider_type"
        const val KEY_ENDPOINT_URL = "ai_endpoint_url"
        const val KEY_API_KEY = "ai_api_key"
        const val KEY_MODEL_NAME = "ai_model_name"
        const val KEY_THREAT_DETECTION = "ai_threat_detection_enabled"
        const val KEY_WRITING_ASSISTANT = "ai_writing_assistant_enabled"
    }
}
