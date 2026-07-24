package net.thunderbird.feature.ai.internal.usecase

import net.thunderbird.core.logging.LogMessage
import net.thunderbird.core.logging.LogTag
import net.thunderbird.core.logging.Logger

class FakeLogger : Logger {
    override fun verbose(tag: LogTag?, throwable: Throwable?, message: () -> LogMessage) = Unit
    override fun debug(tag: LogTag?, throwable: Throwable?, message: () -> LogMessage) = Unit
    override fun info(tag: LogTag?, throwable: Throwable?, message: () -> LogMessage) = Unit
    override fun warn(tag: LogTag?, throwable: Throwable?, message: () -> LogMessage) = Unit
    override fun error(tag: LogTag?, throwable: Throwable?, message: () -> LogMessage) = Unit
}
