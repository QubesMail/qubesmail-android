package net.thunderbird.feature.ai.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.thunderbird.feature.ai.api.domain.model.AiConfig
import net.thunderbird.feature.ai.api.domain.model.AiProviderType
import net.thunderbird.feature.ai.api.repository.AiConfigRepository

internal class AiSettingsViewModel(
    private val configRepository: AiConfigRepository,
) : ViewModel() {

    data class UiState(
        val enabled: Boolean = false,
        val providerType: AiProviderType = AiProviderType.CHAT_GPT,
        val apiKey: String = "",
        val modelName: String = "",
        val endpointUrl: String = "",
        val enableThreatDetection: Boolean = true,
        val enableWritingAssistant: Boolean = true,
        val isSaving: Boolean = false,
        val showSaveSuccess: Boolean = false,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        val config = configRepository.aiConfig.value
        _uiState.value = UiState(
            enabled = config.enabled,
            providerType = config.providerType,
            apiKey = config.apiKey,
            modelName = config.modelName,
            endpointUrl = config.endpointUrl,
            enableThreatDetection = config.enableThreatDetection,
            enableWritingAssistant = config.enableWritingAssistant,
        )
    }

    fun setEnabled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enabled = enabled)
    }

    fun setProvider(provider: AiProviderType) {
        _uiState.value = _uiState.value.copy(
            providerType = provider,
            endpointUrl = provider.defaultEndpointUrl,
            modelName = provider.defaultModelName,
        )
    }

    fun setApiKey(key: String) {
        _uiState.value = _uiState.value.copy(apiKey = key)
    }

    fun setModelName(name: String) {
        _uiState.value = _uiState.value.copy(modelName = name)
    }

    fun setEndpointUrl(url: String) {
        _uiState.value = _uiState.value.copy(endpointUrl = url)
    }

    fun setThreatDetection(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableThreatDetection = enabled)
    }

    fun setWritingAssistant(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableWritingAssistant = enabled)
    }

    fun save() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            val state = _uiState.value
            configRepository.saveAiConfig(
                AiConfig(
                    enabled = state.enabled,
                    providerType = state.providerType,
                    apiKey = state.apiKey,
                    modelName = state.modelName,
                    endpointUrl = state.endpointUrl,
                    enableThreatDetection = state.enableThreatDetection,
                    enableWritingAssistant = state.enableWritingAssistant,
                ),
            )
            _uiState.value = _uiState.value.copy(isSaving = false, showSaveSuccess = true)
        }
    }

    fun dismissSaveSuccess() {
        _uiState.value = _uiState.value.copy(showSaveSuccess = false)
    }
}
