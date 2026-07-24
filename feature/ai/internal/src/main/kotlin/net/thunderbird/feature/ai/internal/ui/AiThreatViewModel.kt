package net.thunderbird.feature.ai.internal.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.thunderbird.core.outcome.Outcome
import net.thunderbird.feature.ai.api.domain.model.AiThreatAnalysis
import net.thunderbird.feature.ai.api.repository.AnalyzeEmailThreatUseCase

internal class AiThreatViewModel(
    private val analyzeEmailThreat: AnalyzeEmailThreatUseCase,
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = false,
        val analysis: AiThreatAnalysis? = null,
        val error: String? = null,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun analyzeEmail(subject: String, sender: String, bodyText: String) {
        if (_uiState.value.isLoading || _uiState.value.analysis != null) return

        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            val result = analyzeEmailThreat(subject, sender, bodyText)
            when (result) {
                is Outcome.Success -> {
                    _uiState.value = UiState(analysis = result.data)
                }
                is Outcome.Failure -> {
                    _uiState.value = UiState(error = result.error.toString())
                }
            }
        }
    }
}
