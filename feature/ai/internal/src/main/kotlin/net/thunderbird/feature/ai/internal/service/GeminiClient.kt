package net.thunderbird.feature.ai.internal.service

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.thunderbird.core.outcome.Outcome
import net.thunderbird.feature.ai.api.domain.model.AiConfig
import net.thunderbird.feature.ai.api.domain.model.AiError

internal class GeminiClient(
    private val httpClient: HttpClient,
) : AiClient {

    override suspend fun executeCompletion(
        config: AiConfig,
        systemInstruction: String,
        userPrompt: String,
    ): Outcome<String, AiError> {
        return try {
            val model = config.modelName
            val baseUrl = config.endpointUrl.trimEnd('/')
            val endpoint = "$baseUrl/v1beta/models/$model:generateContent?key=${config.apiKey}"

            val requestBody = buildJsonObject {
                put("system_instruction", buildJsonObject {
                    put("parts", JsonArray(listOf(
                        buildJsonObject { put("text", JsonPrimitive(systemInstruction)) },
                    )))
                })
                put("contents", JsonArray(listOf(
                    buildJsonObject {
                        put("parts", JsonArray(listOf(
                            buildJsonObject { put("text", JsonPrimitive(userPrompt)) },
                        )))
                    },
                )))
                put("generationConfig", buildJsonObject {
                    put("temperature", JsonPrimitive(0.3))
                    put("responseMimeType", JsonPrimitive("application/json"))
                })
            }

            val response = httpClient.post(endpoint) {
                contentType(ContentType.Application.Json)
                setBody(requestBody.toString())
            }

            if (response.status != HttpStatusCode.OK) {
                return Outcome.failure(
                    AiError.ApiError(response.status.value, response.bodyAsText().take(500)),
                )
            }

            val responseText = response.bodyAsText()
            val responseJson = kotlinx.serialization.json.Json.parseToJsonElement(responseText).jsonObject
            val textContent = responseJson["candidates"]?.jsonArray
                ?.getOrNull(0)?.jsonObject
                ?.get("content")?.jsonObject
                ?.get("parts")?.jsonArray
                ?.getOrNull(0)?.jsonObject
                ?.get("text")?.jsonPrimitive?.content
                ?: return Outcome.failure(AiError.ParseError(IllegalStateException("Empty Gemini response")))

            Outcome.success(textContent)
        } catch (e: Exception) {
            Outcome.failure(AiError.NetworkError(e))
        }
    }
}
