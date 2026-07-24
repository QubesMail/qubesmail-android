package net.thunderbird.feature.ai.internal.service

internal object AiPromptBuilder {

    private const val MAX_BODY_LENGTH = 3000

    fun buildThreatAnalysisPrompt(
        subject: String,
        sender: String,
        bodyText: String,
    ): String {
        val sanitizedBody = bodyText.take(MAX_BODY_LENGTH)
        return """
            Analyze the following email for security threats including phishing, malware, 
            social engineering, spoofed sender addresses, and malicious URLs or attachments.
            
            Respond ONLY with a valid JSON object (no markdown, no code fences) containing:
            - "level": exactly one of "SAFE", "WARNING", "SUSPICIOUS", "DANGER"
            - "summary": a concise 1-2 sentence safety assessment
            - "details": an array of strings, each describing a specific concern found
            
            If the email appears safe, the source is authentic, and links are not suspicious, set level to "SAFE", set the summary to "Everything OK. Source is authentic and links are not suspicious.", and set details to an empty array.
            
            Email to analyze:
            From: $sender
            Subject: $subject
            Body:
            $sanitizedBody
        """.trimIndent()
    }

    fun buildWritingSuggestionPrompt(
        subject: String,
        sender: String,
        incomingBody: String,
        promptOrTone: String,
    ): String {
        val sanitizedBody = incomingBody.take(MAX_BODY_LENGTH)
        return """
            Draft a reply to the following email. Follow the user's tone instruction: "$promptOrTone".
            
            Respond ONLY with a valid JSON object (no markdown, no code fences) containing:
            - "replyText": the full reply email body text
            - "suggestedTone": a short description of the tone used
            - "bulletPoints": an array of strings summarizing key points of the reply
            
            Original email to reply to:
            From: $sender
            Subject: $subject
            Body:
            $sanitizedBody
        """.trimIndent()
    }

    fun buildContinueWritingPrompt(
        subject: String,
        sender: String,
        incomingBody: String,
        currentDraft: String,
    ): String {
        val sanitizedBody = incomingBody.take(MAX_BODY_LENGTH)
        val sanitizedDraft = currentDraft.take(MAX_BODY_LENGTH)
        return """
            The user is drafting a reply to the following email. Read what they have typed so far and generate the NEXT logical sentence or paragraph to continue their thought smoothly.
            
            Respond ONLY with a valid JSON object (no markdown, no code fences) containing:
            - "replyText": exactly the new text to append (do NOT include the existing text, ONLY the continuation)
            - "suggestedTone": a short description of the tone used
            - "bulletPoints": an array with one string summarizing what you added
            
            Original email to reply to:
            From: $sender
            Subject: $subject
            Body:
            $sanitizedBody
            
            User's current draft:
            $sanitizedDraft
        """.trimIndent()
    }

    const val THREAT_SYSTEM_INSTRUCTION = "You are a cybersecurity expert specializing in email threat analysis. You respond only with valid JSON."
    const val WRITING_SYSTEM_INSTRUCTION = "You are a professional email writing assistant. You respond only with valid JSON."
}
