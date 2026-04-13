package com.zekaoformani.macera.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zekaoformani.macera.R
import com.zekaoformani.macera.data.models.characters
import kotlinx.coroutines.delay

@Composable
fun TutorialScreen(
    characterId: Int = 1,
    onComplete: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }

    val charRes = characters.find { it.id == characterId }?.imageRes ?: R.drawable.character_1

    // ── Mini Fizik Simülasyonu (Tap-to-learn) ──
    val gravity = 0.7f
    val jumpForce = -14f
    var charY by remember { mutableFloatStateOf(0f) }
    var charVelY by remember { mutableFloatStateOf(0f) }
    var hasTapped by remember { mutableStateOf(false) }
    var tapCount by remember { mutableIntStateOf(0) }

    // Arka plan parallax
    var bgOffset by remember { mutableFloatStateOf(0f) }

    // Mini fizik döngüsü
    LaunchedEffect(Unit) {
        while (true) {
            delay(16L)
            charVelY += gravity
            charY += charVelY
            bgOffset -= 3f
            if (bgOffset <= -screenWidthPx) bgOffset += screenWidthPx
            // Zemin sınırı (tutorial alanı ortası)
            if (charY < -80f) { charY = -80f; charVelY = 0f }
            if (charY > 80f) { charY = 80f; charVelY = -8f } // Zemine değince hafif zıpla
        }
    }

    // Velocity tabanlı eğim
    val targetTilt = (charVelY * -2f).coerceIn(-40f, 40f)
    val smoothTilt by animateFloatAsState(targetValue = targetTilt, animationSpec = tween(50), label = "tilt")

    // El ikonu nabzı
    val infiniteTransition = rememberInfiniteTransition(label = "tut")
    val handScale by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(600, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "hand"
    )
    val skipGlow by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "skipGlow"
    )

    // Engel animasyonu (küçük boru illüstrasyonu)
    val pipeX by infiniteTransition.animateFloat(
        initialValue = 1.15f, targetValue = -0.3f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart),
        label = "pipeX"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    charVelY = jumpForce
                    hasTapped = true
                    tapCount++
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }
    ) {
        // ── 1. PARALLAX ARKA PLAN (NET, bulanıklık yok) ──
        val scrollX = bgOffset
        Image(
            painter = painterResource(R.drawable.chapter1_theme),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize().offset(x = with(density) { scrollX.toDp() })
        )
        Image(
            painter = painterResource(R.drawable.chapter1_theme),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize().offset(x = with(density) { (scrollX + screenWidthPx).toDp() })
        )

        // Hafif karartma overlay (okunabilirlik için)
        Box(
            Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        // ── 2. ÜST BAR ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Adım indikatörleri
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.size(width = 28.dp, height = 8.dp).clip(CircleShape).background(Color(0xFFFF8C00)))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color.White.copy(0.3f)))
                Box(Modifier.size(8.dp).clip(CircleShape).background(Color.White.copy(0.3f)))
            }
            // ATLA butonu
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(48.dp))
                    .background(Brush.horizontalGradient(listOf(
                        Color(0xFFFF8C00).copy(alpha = skipGlow),
                        Color(0xFFFF5722).copy(alpha = skipGlow)
                    )))
                    .border(2.dp, Color.White.copy(0.5f), RoundedCornerShape(48.dp))
                    .clickable { onComplete() }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_skip),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // ── 3. ORTA ALAN: Kahraman + Mini Engel Önizlemesi ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.65f)
                .align(Alignment.TopCenter)
                .padding(top = 80.dp),
            contentAlignment = Alignment.Center
        ) {
            // Mini boru önizlemesi (sağdan sola geçen)
            val pipeScreenX = (pipeX * configuration.screenWidthDp).dp
            val pipeW = 55.dp
            val gapH = 160.dp

            // Üst boru
            Box(
                modifier = Modifier
                    .offset(x = pipeScreenX, y = 0.dp)
                    .width(pipeW)
                    .height(100.dp)
                    .align(Alignment.TopStart)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF1B5E20))),
                        RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                    )
                    .border(2.dp, Color(0xFF76FF03).copy(0.6f), RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            )
            // Alt boru
            Box(
                modifier = Modifier
                    .offset(x = pipeScreenX, y = 0.dp)
                    .width(pipeW)
                    .height(80.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF5D4037), Color(0xFF795548), Color(0xFF5D4037))),
                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
                    .border(2.dp, Color(0xFFFFD54F).copy(0.5f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            )

            // Kahraman — şeffaf sprite, kutu/kart yok
            val tutorialFrame = when {
                charVelY < -1.5f -> 2 // Up
                charVelY > 1.5f -> 0  // Down
                else -> 1             // Mid
            }

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .offset(y = with(density) { charY.toDp() })
                    .graphicsLayer { rotationZ = smoothTilt },
                contentAlignment = Alignment.Center
            ) {
                // Kutu/Çerçeve olmadan sadece karakter (Sprite Slicing)
                Image(
                    painter = painterResource(charRes),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight,
                    alignment = when(tutorialFrame) {
                        0 -> Alignment.CenterStart
                        1 -> Alignment.Center
                        else -> Alignment.CenterEnd
                    },
                    modifier = Modifier.fillMaxHeight().fillMaxWidth(3f)
                )
            }

            // El/Tap göstergesi (henüz tap edilmediyse göster)
            if (!hasTapped || tapCount < 3) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFFF8C00).copy(0.88f), CircleShape)
                            .graphicsLayer { scaleX = handScale; scaleY = handScale },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.TouchApp, null, tint = Color.White, modifier = Modifier.size(36.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        if (!hasTapped) stringResource(R.string.tutorial_instruction_tap) 
                        else stringResource(R.string.tutorial_instruction_success, tapCount),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 8f))
                    )
                }
            }
        }

        // ── 4. ALT BİLGİ KARTI (Glassmorphism, minimal) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Color.White.copy(alpha = 0.12f),
                    RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                )
                .border(1.dp, Color.White.copy(0.25f), RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Başlık
                Text(
                    text = stringResource(R.string.tutorial_tap_label),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 6f))
                )
                Text(
                    text = "👆 " + stringResource(R.string.tutorial_tap_action),
                    color = Color(0xFFFF8C00),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(shadow = Shadow(Color(0xFFE65100), blurRadius = 10f))
                )

                // Açıklama
                Text(
                    text = stringResource(R.string.tutorial_description),
                    color = Color.White.copy(0.85f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(Modifier.height(4.dp))

                // BAŞLA Butonu
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(48.dp))
                        .background(Brush.horizontalGradient(listOf(Color(0xFFFB923C), Color(0xFFEA580C))))
                        .border(1.5.dp, Color(0xFFC2410C), RoundedCornerShape(48.dp))
                        .clickable { onComplete() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_start_play),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Icon(Icons.Default.ChevronRight, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                }
            }
        }
    }
}
