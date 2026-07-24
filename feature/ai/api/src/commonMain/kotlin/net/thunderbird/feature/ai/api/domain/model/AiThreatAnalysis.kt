package net.thunderbird.feature.ai.api.domain.model

enum class ThreatLevel {
    SAFE,
    WARNING,
    SUSPICIOUS,
    DANGER,
}

data class AiThreatAnalysis(
    val level: ThreatLevel,
    val summary: String,
    val details: List<String> = emptyList(),
)
