package net.thunderbird.feature.ai.internal.service

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.thunderbird.core.outcome.Outcome
import net.thunderbird.feature.ai.api.domain.model.AiError
import net.thunderbird.feature.ai.api.domain.model.AiThreatAnalysis
import net.thunderbird.feature.ai.api.domain.model.AiWritingSuggestion
import net.thunderbird.feature.ai.api.domain.model.ThreatLevel

internal object AiResponseParser {

    private val json = Json { ignoreUnknownKeys = true }

    fun parseThreatAnalysis(rawContent: String): Outcome<AiThreatAnalysis, AiError> {
        return try {
            val jsonString = extractJsonObject(rawContent)
            val parsedObj = json.parseToJsonElement(jsonString).jsonObject

            val levelStr = parsedObj["level"]?.jsonPrimitive?.content?.uppercase() ?: "SAFE"
            val level = try {
                ThreatLevel.valueOf(levelStr)
            } catch (_: IllegalArgumentException) {
                ThreatLevel.SAFE
            }
            val summary = parsedObj["summary"]?.jsonPrimitive?.content ?: "Analysis complete."
            val details = parsedObj["details"]?.jsonArray
                ?.mapNotNull { runCatching { it.jsonPrimitive.content }.getOrNull() }
                ?: emptyList()

            Outcome.success(
                AiThreatAnalysis(
                    level = level,
                    summary = summary,
                    details = details,
                ),
            )
        } catch (e: Exception) {
            Outcome.failure(AiError.ParseError(e))
        }
    }

    fun parseWritingSuggestion(rawContent: String): Outcome<AiWritingSuggestion, AiError> {
        return try {
            val jsonString = extractJsonObject(rawContent)
            val parsedObj = json.parseToJsonElement(jsonString).jsonObject

            val replyText = parsedObj["replyText"]?.jsonPrimitive?.content ?: rawContent
            val suggestedTone = parsedObj["suggestedTone"]?.jsonPrimitive?.content ?: "Professional"
            val bulletPoints = parsedObj["bulletPoints"]?.jsonArray
                ?.mapNotNull { runCatching { it.jsonPrimitive.content }.getOrNull() }
                ?: emptyList()

            Outcome.success(
                AiWritingSuggestion(
                    replyText = replyText,
                    suggestedTone = suggestedTone,
                    bulletPoints = bulletPoints,
                ),
            )
        } catch (e: Exception) {
            Outcome.failure(AiError.ParseError(e))
        }
    }

    private fun extractJsonObject(raw: String): String {
        val trimmed = raw.trim()
        val start = trimmed.indexOf('{')
        val end = trimmed.lastIndexOf('}')
        if (start == -1 || end == -1 || start >= end) {
            throw IllegalArgumentException("No JSON object found in response")
        }
        return trimmed.substring(start, end + 1)
    }
}
