package net.thunderbird.feature.ai.internal.usecase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import net.thunderbird.core.outcome.Outcome
import net.thunderbird.feature.ai.api.domain.model.AiConfig
import net.thunderbird.feature.ai.api.domain.model.AiError
import net.thunderbird.feature.ai.api.domain.model.AiProviderType
import net.thunderbird.feature.ai.api.domain.model.ThreatLevel
import net.thunderbird.feature.ai.api.repository.AiConfigRepository
import net.thunderbird.feature.ai.internal.service.AiClient
import net.thunderbird.feature.ai.internal.service.AiClientProvider
import org.junit.Test

class DefaultAnalyzeEmailThreatUseCaseTest {

    @Test
    fun `when AI is disabled then returns SAFE without network call`() = runTest {
        val testSubject = createTestSubject(
            config = AiConfig(enabled = false),
        )

        val result = testSubject("Subject", "sender@test.com", "Hello")

        assertThat(result).isInstanceOf<Outcome.Success<*>>()
        val analysis = (result as Outcome.Success).data
        assertThat(analysis.level).isEqualTo(ThreatLevel.SAFE)
    }

    @Test
    fun `when API key is blank then returns MissingApiKey error`() = runTest {
        val testSubject = createTestSubject(
            config = AiConfig(enabled = true, enableThreatDetection = true, apiKey = ""),
        )

        val result = testSubject("Subject", "sender@test.com", "Hello")

        assertThat(result).isInstanceOf<Outcome.Failure<*>>()
        assertThat((result as Outcome.Failure).error).isEqualTo(AiError.MissingApiKey)
    }

    @Test
    fun `when AI client returns valid JSON then parses threat analysis`() = runTest {
        val fakeResponse = """{
            "level": "SUSPICIOUS",
            "summary": "Suspicious link found",
            "details": ["Unverified domain"]
        }"""
        val testSubject = createTestSubject(
            config = AiConfig(
                enabled = true,
                enableThreatDetection = true,
                apiKey = "test-key",
                providerType = AiProviderType.CHAT_GPT,
            ),
            clientResponse = Outcome.success(fakeResponse),
        )

        val result = testSubject("Urgent", "scammer@bad.com", "Click here")

        assertThat(result).isInstanceOf<Outcome.Success<*>>()
        val analysis = (result as Outcome.Success).data
        assertThat(analysis.level).isEqualTo(ThreatLevel.SUSPICIOUS)
        assertThat(analysis.summary).isEqualTo("Suspicious link found")
    }

    @Test
    fun `when AI client returns network error then returns NetworkError`() = runTest {
        val testSubject = createTestSubject(
            config = AiConfig(
                enabled = true,
                enableThreatDetection = true,
                apiKey = "test-key",
            ),
            clientResponse = Outcome.failure(AiError.NetworkError(RuntimeException("timeout"))),
        )

        val result = testSubject("Subject", "sender@test.com", "Hello")

        assertThat(result).isInstanceOf<Outcome.Failure<*>>()
        assertThat((result as Outcome.Failure).error).isInstanceOf<AiError.NetworkError>()
    }

    private fun createTestSubject(
        config: AiConfig = AiConfig(),
        clientResponse: Outcome<String, AiError> = Outcome.success("{\"level\":\"SAFE\",\"summary\":\"OK\",\"details\":[]}"),
    ): DefaultAnalyzeEmailThreatUseCase {
        val fakeConfigRepo = object : AiConfigRepository {
            override val aiConfig: StateFlow<AiConfig> = MutableStateFlow(config)
            override suspend fun saveAiConfig(config: AiConfig) = Unit
        }
        val fakeClient = object : AiClient {
            override suspend fun executeCompletion(
                config: AiConfig,
                systemInstruction: String,
                userPrompt: String,
            ) = clientResponse
        }
        val fakeClientProvider = AiClientProvider { _ -> fakeClient }
        val fakeLogger = FakeLogger()

        return DefaultAnalyzeEmailThreatUseCase(fakeConfigRepo, fakeClientProvider, fakeLogger)
    }
}
