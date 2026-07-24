package net.thunderbird.feature.ai.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.thunderbird.core.outcome.Outcome
import net.thunderbird.feature.ai.api.domain.model.AiWritingSuggestion
import net.thunderbird.feature.ai.api.repository.ContinueWritingUseCase
import net.thunderbird.feature.ai.api.repository.GenerateWritingSuggestionUseCase

internal class AiWritingViewModel(
    private val generateWritingSuggestion: GenerateWritingSuggestionUseCase,
    private val continueWritingUseCase: ContinueWritingUseCase,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val promptText: String = "Professional",
        val suggestion: AiWritingSuggestion? = null,
        val error: String? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun setPrompt(text: String) {
        _uiState.value = _uiState.value.copy(promptText = text)
    }

    fun generateReply(subject: String, sender: String, body: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = generateWritingSuggestion(
                subject = subject,
                sender = sender,
                incomingBody = body,
                promptOrTone = _uiState.value.promptText,
            )
            when (result) {
                is Outcome.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        suggestion = result.data,
                    )
                }
                is Outcome.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to generate reply: ${result.error}",
                    )
                }
            }
        }
    }
    fun continueWriting(subject: String, sender: String, body: String, currentDraft: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, suggestion = null)
            val result = continueWritingUseCase(
                subject = subject,
                sender = sender,
                incomingBody = body,
                currentDraft = currentDraft,
            )
            when (result) {
                is Outcome.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        suggestion = result.data,
                    )
                }
                is Outcome.Failure -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to continue writing: ${result.error}",
                    )
                }
            }
        }
    }
}
