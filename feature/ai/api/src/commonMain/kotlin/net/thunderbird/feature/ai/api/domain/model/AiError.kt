package net.thunderbird.feature.ai.api.domain.model

sealed interface AiError {
    data object Disabled : AiError
    data object MissingApiKey : AiError
    data class NetworkError(val cause: Throwable) : AiError
    data class ApiError(val statusCode: Int, val message: String) : AiError
    data class ParseError(val cause: Throwable) : AiError
    data class UnknownError(val cause: Throwable) : AiError
}
