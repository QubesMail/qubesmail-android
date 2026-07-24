package net.thunderbird.feature.ai.internal.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class AiSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiSettingsScreen(
                onBack = { finish() },
            )
        }
    }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, AiSettingsActivity::class.java)
        }
    }
}
