package com.zekaoformani.macera.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.zekaoformani.macera.R
import com.zekaoformani.macera.ui.components.BtnPlay

@Composable
fun MainMenuScreen(
    totalScore: Int = 0,
    onNavigateToCharacterSelection: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToBadges: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mainMenuAnim")

    // Tilki yukarı-aşağı bounce animasyonu
    val foxOffsetY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "foxBounce"
    )

    // Başlık hafif sallanma
    val titleOffsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "titleFloat"
    )

    val profileGlow by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "profileGlow"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1- Arka plan: Orman görseli (tam ekran)
        Image(
            painter = painterResource(id = R.drawable.chapter1_theme),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2- Karanlık vignette katmanı
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.40f))
        )

        // 3- Üst Bar: Settings + Lang (sol) + Skor Tablosu (sağ)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Ayarlar Butonu
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.btn_settings),
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Dil Değiştir Butonu
                IconButton(
                    onClick = {
                        val currentLocales = AppCompatDelegate.getApplicationLocales()
                        val isTr = currentLocales.toLanguageTags().contains("tr") || java.util.Locale.getDefault().language == "tr" && currentLocales.isEmpty
                        val newLocale = if (isTr) "en" else "tr"
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(newLocale))
                    },
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Switch Language",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Ortada: çocuklara yönelik parlak skor rozeti (dokunması kolay, casual oyun stili)
            Surface(
                color = Color(0xFFFFCA28).copy(alpha = 0.38f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFEE58)),
                shadowElevation = 10.dp,
                modifier = Modifier.graphicsLayer {
                    scaleX = profileGlow
                    scaleY = profileGlow
                }
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.lbl_your_score),
                        color = Color(0xFFFFFDE7),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.45f), blurRadius = 4f))
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = stringResource(R.string.cd_profile_score),
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$totalScore",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.55f), blurRadius = 6f))
                        )
                    }
                }
            }

            // Sağ: Rozetler + Skor Tablosu
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = onNavigateToBadges,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = stringResource(R.string.btn_badges),
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(28.dp)
                    )
                }
                IconButton(
                    onClick = onNavigateToLeaderboard,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = stringResource(R.string.btn_leaderboard),
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // 4- Ana içerik: Başlık + Tilki + Oyna Butonu
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // MACERA ORMANI başlığı (animasyonlu)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = titleOffsetY.dp)
            ) {
                Text(
                    text = stringResource(R.string.title_adventure),
                    color = Color.White,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFF00E5FF), // Crystal Cave Luminous Cyan
                            blurRadius = 32f
                        )
                    )
                )
                Text(
                    text = stringResource(R.string.title_forest),
                    color = Color(0xFFFF8C00),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color(0xFFFFD700), // Luminous Gold
                            blurRadius = 32f
                        )
                    ),
                    modifier = Modifier.offset(y = (-10).dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🦊 TİLKİ — Çerçevesiz, sadece görsel, bouncing
            Image(
                painter = painterResource(id = R.drawable.character_1),
                contentDescription = stringResource(R.string.cd_fox_mascot),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(220.dp)
                    .offset(y = foxOffsetY.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // OYNA butonu
            BtnPlay(
                text = stringResource(R.string.btn_play),
                onClick = onNavigateToCharacterSelection,
                modifier = Modifier
                    .width(280.dp)
                    .height(80.dp)
            )
        }
    }
}
