package net.thunderbird.feature.ai.internal.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotEmpty
import net.thunderbird.core.outcome.Outcome
import net.thunderbird.feature.ai.api.domain.model.AiError
import net.thunderbird.feature.ai.api.domain.model.ThreatLevel
import org.junit.Test

class AiResponseParserTest {

    @Test
    fun `parseThreatAnalysis with valid JSON`() {
        val raw = """{ "level": "DANGER", "summary": "Phishing detected", "details": ["Spoofed URL"] }"""

        val result = AiResponseParser.parseThreatAnalysis(raw)

        assertThat(result).isInstanceOf<Outcome.Success<*>>()
        val analysis = (result as Outcome.Success).data
        assertThat(analysis.level).isEqualTo(ThreatLevel.DANGER)
        assertThat(analysis.summary).isEqualTo("Phishing detected")
        assertThat(analysis.details).isNotEmpty()
    }

    @Test
    fun `parseThreatAnalysis with markdown-wrapped JSON`() {
        val raw = """
