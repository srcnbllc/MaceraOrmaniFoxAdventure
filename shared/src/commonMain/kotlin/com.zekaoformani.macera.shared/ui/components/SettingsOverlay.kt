package com.zekaoformani.macera.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun SettingsOverlay(
    musicEnabled: Boolean,
    sfxEnabled: Boolean,
    onMusicToggle: (Boolean) -> Unit,
    onSfxToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                // Oyuna uygun ahşap rengi arka plan
                .background(Brush.verticalGradient(listOf(Color(0xFF4E342E), Color(0xFF2E1A14))))
                // Altın rengi çerçeve
                .border(4.dp, Color(0xFFFFB300), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "AYARLAR",
                    color = Color(0xFFFFD700),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(32.dp))

                // --- MÜZİK AYARI ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Arka Plan Müziği", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = musicEnabled,
                        onCheckedChange = onMusicToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF69F0AE), // Aktifken parlak yeşil
                            checkedTrackColor = Color(0xFF69F0AE).copy(alpha = 0.4f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- SES EFEKTLERİ AYARI ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ses Efektleri", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Switch(
                        checked = sfxEnabled,
                        onCheckedChange = onSfxToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF69F0AE),
                            checkedTrackColor = Color(0xFF69F0AE).copy(alpha = 0.4f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // --- KAPAT BUTONU ---
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Kırmızı çıkış butonu
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("KAPAT", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}