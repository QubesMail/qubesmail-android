package net.thunderbird.feature.ai.internal.service

import io.ktor.client.HttpClient
import io.ktor.client.request.header
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

internal class OpenAiClient(
    private val httpClient: HttpClient,
) : AiClient {

    override suspend fun executeCompletion(
        config: AiConfig,
        systemInstruction: String,
        userPrompt: String,
    ): Outcome<String, AiError> {
        return try {
            val requestBody = buildJsonObject {
                put("model", JsonPrimitive(config.modelName))
                put("messages", JsonArray(listOf(
                    buildJsonObject {
                        put("role", JsonPrimitive("system"))
                        put("content", JsonPrimitive(systemInstruction))
                    },
                    buildJsonObject {
                        put("role", JsonPrimitive("user"))
                        put("content", JsonPrimitive(userPrompt))
                    },
                )))
                put("temperature", JsonPrimitive(0.3))
            }

            val endpoint = config.endpointUrl.trimEnd('/') + "/chat/completions"
            val response = httpClient.post(endpoint) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer ${config.apiKey}")
                setBody(requestBody.toString())
            }

            if (response.status != HttpStatusCode.OK) {
                return Outcome.failure(
                    AiError.ApiError(response.status.value, response.bodyAsText().take(500)),
                )
            }

            val responseText = response.bodyAsText()
            val responseJson = kotlinx.serialization.json.Json.parseToJsonElement(responseText).jsonObject
            val content = responseJson["choices"]?.jsonArray
                ?.getOrNull(0)?.jsonObject
                ?.get("message")?.jsonObject
                ?.get("content")?.jsonPrimitive?.content
                ?: return Outcome.failure(AiError.ParseError(IllegalStateException("Empty API response")))

            Outcome.success(content)
        } catch (e: Exception) {
            Outcome.failure(AiError.NetworkError(e))
        }
    }
}
