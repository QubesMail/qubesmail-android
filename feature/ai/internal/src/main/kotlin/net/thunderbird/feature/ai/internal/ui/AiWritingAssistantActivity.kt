package net.thunderbird.feature.ai.internal.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel

class AiWritingAssistantActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val subject = intent.getStringExtra(EXTRA_SUBJECT).orEmpty()
        val sender = intent.getStringExtra(EXTRA_SENDER).orEmpty()
        val body = intent.getStringExtra(EXTRA_BODY).orEmpty()
        val isContinueMode = intent.getBooleanExtra(EXTRA_IS_CONTINUE_MODE, false)
        val currentDraft = intent.getStringExtra(EXTRA_CURRENT_DRAFT).orEmpty()

        setContent {
            AiWritingAssistantScreen(
                subject = subject,
                sender = sender,
                body = body,
                isContinueMode = isContinueMode,
                currentDraft = currentDraft,
                onInsert = { replyText ->
                    val resultIntent = Intent()
                    resultIntent.putExtra(EXTRA_RESULT_TEXT, replyText)
                    resultIntent.putExtra(EXTRA_IS_CONTINUE_MODE, isContinueMode)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                },
                onBack = { finish() },
            )
        }
    }

    companion object {
        const val EXTRA_SUBJECT = "ai_subject"
        const val EXTRA_SENDER = "ai_sender"
        const val EXTRA_BODY = "ai_body"
        const val EXTRA_RESULT_TEXT = "ai_result_text"
        const val EXTRA_IS_CONTINUE_MODE = "ai_is_continue_mode"
        const val EXTRA_CURRENT_DRAFT = "ai_current_draft"

        fun createIntent(
            context: Context,
            subject: String,
            sender: String,
            body: String,
            isContinueMode: Boolean = false,
            currentDraft: String = "",
        ): Intent {
            return Intent(context, AiWritingAssistantActivity::class.java).apply {
                putExtra(EXTRA_SUBJECT, subject)
                putExtra(EXTRA_SENDER, sender)
                putExtra(EXTRA_BODY, body)
                putExtra(EXTRA_IS_CONTINUE_MODE, isContinueMode)
                putExtra(EXTRA_CURRENT_DRAFT, currentDraft)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AiWritingAssistantScreen(
    subject: String,
    sender: String,
    body: String,
    isContinueMode: Boolean,
    currentDraft: String,
    onInsert: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: AiWritingViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(isContinueMode) {
        if (isContinueMode) {
            viewModel.continueWriting(subject, sender, body, currentDraft)
        }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (isContinueMode) "AI: Suggest Next" else "AI Writing Assistant") },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Replying to: $subject",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                if (!isContinueMode) {
                    OutlinedTextField(
                        value = state.promptText,
                        onValueChange = viewModel::setPrompt,
                        label = { Text("Tone / Instructions") },
                        placeholder = { Text("e.g. Professional, Friendly, Brief...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                    )
                }

                Button(
                    onClick = {
                        if (isContinueMode) {
                            viewModel.continueWriting(subject, sender, body, currentDraft)
                        } else {
                            viewModel.generateReply(subject, sender, body)
                        }
                    },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (state.isLoading) "Generating..." else (if (isContinueMode) "Suggest Again" else "Generate Reply"))
                }

                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }

                state.error?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = error,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }

                state.suggestion?.let { suggestion ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Tone: ${suggestion.suggestedTone}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = suggestion.replyText,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.generateReply(subject, sender, body) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Regenerate")
                        }
                        Button(
                            onClick = { onInsert(suggestion.replyText) },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Insert Reply")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
