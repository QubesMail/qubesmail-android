package net.thunderbird.feature.ai.internal.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.thunderbird.feature.ai.api.domain.model.AiThreatAnalysis
import net.thunderbird.feature.ai.api.domain.model.ThreatLevel

@Composable
fun AiThreatBannerContent(
    isLoading: Boolean,
    analysis: AiThreatAnalysis?,
    error: String?,
) {
    if (isLoading) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFE3F2FD),
            ),
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(8.dp))
                Text("Analyzing email for threats...", style = MaterialTheme.typography.bodySmall)
            }
        }
    }

    analysis?.let { result ->

        val (backgroundColor, textColor, emoji) = when (result.level) {
            ThreatLevel.SAFE -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "\u2705")
            ThreatLevel.WARNING -> Triple(Color(0xFFFFF8E1), Color(0xFFF57F17), "\u26A0\uFE0F")
            ThreatLevel.SUSPICIOUS -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "\uD83D\uDEA8")
            ThreatLevel.DANGER -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "\u26D4")
        }

        var expanded by remember { mutableStateOf(result.level != ThreatLevel.SAFE) }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clickable { expanded = !expanded },
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "AI: ${result.level.name}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = if (expanded) "\u25B2" else "\u25BC",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Text(
                    text = result.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = textColor,
                    modifier = Modifier.padding(top = 4.dp),
                )
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically(),
                ) {
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        result.details.forEach { detail ->
                            Text(
                                text = "\u2022 $detail",
                                style = MaterialTheme.typography.bodySmall,
                                color = textColor,
                                modifier = Modifier.padding(start = 8.dp, top = 2.dp),
                            )
                        }
                    }
                }
            }
        }
    }

    error?.let {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFFFF3E0),
            ),
        ) {
            Text(
                text = "AI analysis unavailable",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFFE65100),
            )
        }
    }
}
