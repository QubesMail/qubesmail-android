package net.thunderbird.feature.ai.internal.inject

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import net.thunderbird.feature.ai.api.repository.AiConfigRepository
import net.thunderbird.feature.ai.api.repository.AnalyzeEmailThreatUseCase
import net.thunderbird.feature.ai.api.repository.ContinueWritingUseCase
import net.thunderbird.feature.ai.api.repository.GenerateWritingSuggestionUseCase
import net.thunderbird.feature.ai.internal.repository.DefaultAiConfigRepository
import net.thunderbird.feature.ai.internal.service.AiClientProvider
import net.thunderbird.feature.ai.internal.service.DefaultAiClientProvider
import net.thunderbird.feature.ai.internal.ui.AiSettingsViewModel
import net.thunderbird.feature.ai.internal.ui.AiThreatViewModel
import net.thunderbird.feature.ai.internal.ui.AiWritingViewModel
import net.thunderbird.feature.ai.internal.usecase.DefaultAnalyzeEmailThreatUseCase
import net.thunderbird.feature.ai.internal.usecase.DefaultContinueWritingUseCase
import net.thunderbird.feature.ai.internal.usecase.DefaultGenerateWritingSuggestionUseCase
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val featureAiModule = module {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    },
                )
            }
        }
    }

    single<AiClientProvider> { DefaultAiClientProvider(get()) }
    single<AiConfigRepository> { DefaultAiConfigRepository(get(), get(), get()) }
    factory<AnalyzeEmailThreatUseCase> { DefaultAnalyzeEmailThreatUseCase(get(), get(), get()) }
    factory<GenerateWritingSuggestionUseCase> { DefaultGenerateWritingSuggestionUseCase(get(), get(), get()) }
    factory<ContinueWritingUseCase> { DefaultContinueWritingUseCase(get(), get(), get()) }

    viewModel { AiSettingsViewModel(get()) }
    viewModel { AiThreatViewModel(get()) }
    viewModel { AiWritingViewModel(get(), get()) }
}
