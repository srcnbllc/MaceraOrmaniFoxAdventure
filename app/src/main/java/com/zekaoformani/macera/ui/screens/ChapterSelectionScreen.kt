package com.zekaoformani.macera.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zekaoformani.macera.R
import com.zekaoformani.macera.data.models.arcadeIconRes
import com.zekaoformani.macera.data.models.characters
import com.zekaoformani.macera.ui.components.BtnBack

@Composable
fun ChapterSelectionScreen(
    characterId: Int = 1,
    unlockedChapter: Int = 1,
    levelStars: (Int) -> Int = { 0 },
    levelCompleted: (Int) -> Boolean = { false },
    onNavigateBack: () -> Unit,
    onChapterSelected: (Int) -> Unit
) {
    val charRes = characters.find { it.id == characterId }?.imageRes ?: R.drawable.character_1
    val scrollState = rememberScrollState()
    val infiniteTransition = rememberInfiniteTransition(label = "mapAnim")
    
    val levelsList = com.zekaoformani.macera.data.models.levels
    val nodeSpacing = 280.dp
    val totalMapHeight = (levelsList.size * nodeSpacing.value + 400f).dp

    // Animating Clouds/Stars
    val cloudOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing), RepeatMode.Restart),
        label = "cloud"
    )

    // Character float
    val charFloatY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -15f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "charFloat"
    )

    val levelNodePulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "levelNodePulse"
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val w = maxWidth
        
        // Node pozisyonlarını dinamik üret (Zikzak)
        val nodePositions = remember(w) {
            levelsList.mapIndexed { index, _ ->
                val xFactor = if (index % 2 == 0) 0.3f else 0.7f
                Pair(w * xFactor, (index * nodeSpacing.value + 250f).dp)
            }
        }

        // 1. Arka Plan (Dünyaya göre renk/görsel değişebilir ama şu an genel orman parallax)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1B5E20)) // Deep Forest Base
        ) {
            val parallaxY = -(scrollState.value * 0.25f)
            Image(
                painter = painterResource(id = R.drawable.chapter1_theme),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { translationY = parallaxY }
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
        }

        // 2. Bulutlar
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(Color.White.copy(alpha=0.3f), radius = 60f, center = Offset((cloudOffset % (size.width + 200)) - 100f, size.height * 0.15f))
            drawCircle(Color.White.copy(alpha=0.15f), radius = 90f, center = Offset(((cloudOffset * 1.3f) % (size.width + 300)) - 150f, size.height * 0.45f))
        }

        // Scrollable Harita İçeriği
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(totalMapHeight))

            // Patika Çizgisi
            Canvas(modifier = Modifier.matchParentSize()) {
                val path = Path()
                nodePositions.forEachIndexed { i, pos ->
                    val px = pos.first.toPx()
                    val py = pos.second.toPx()
                    if (i == 0) path.moveTo(px, py)
                    else {
                        val prevX = nodePositions[i - 1].first.toPx()
                        val prevY = nodePositions[i - 1].second.toPx()
                        path.cubicTo(prevX, prevY + 150f, px, py - 150f, px, py)
                    }
                }
                drawPath(
                    path = path,
                    color = Color(0xFFFDD835).copy(alpha = 0.5f),
                    style = Stroke(width = 35f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                drawPath(
                    path = path,
                    color = Color(0xFFFFF176),
                    style = Stroke(width = 15f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // Düğümler ve Dünya Etiketleri
            levelsList.forEachIndexed { i, level ->
                val isLocked = level.id > unlockedChapter
                val starsEarned = if (!isLocked) levelStars(level.id).coerceIn(0, 3) else 0
                val isCompleted = if (!isLocked) levelCompleted(level.id) else false
                val pos = nodePositions[i]
                val posX = pos.first
                val posY = pos.second
                
                // Dünya Değişim Noktasında Başlık Göster
                val prevLevel = if (i > 0) levelsList[i-1] else null
                if (prevLevel == null || prevLevel.world != level.world) {
                    val worldTitle = when(level.world) {
                        com.zekaoformani.macera.data.models.WorldType.FOREST -> R.string.world_1_name
                        com.zekaoformani.macera.data.models.WorldType.BEACH -> R.string.world_2_name
                        com.zekaoformani.macera.data.models.WorldType.SKY -> R.string.world_3_name
                        com.zekaoformani.macera.data.models.WorldType.CRYSTAL -> R.string.world_4_name
                    }
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .offset(y = posY - 120.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Image(
                                painter = painterResource(id = level.world.arcadeIconRes()),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(worldTitle),
                                color = Color(0xFFFFD54F),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Node Circle
                val nodeColor = when(level.world) {
                    com.zekaoformani.macera.data.models.WorldType.FOREST -> Color(0xFF43A047)
                    com.zekaoformani.macera.data.models.WorldType.BEACH -> Color(0xFFFFB300)
                    com.zekaoformani.macera.data.models.WorldType.SKY -> Color(0xFF03A9F4)
                    com.zekaoformani.macera.data.models.WorldType.CRYSTAL -> Color(0xFF7E57C2)
                }

                val nodeScale = when {
                    isLocked -> 1f
                    level.id == unlockedChapter -> levelNodePulse
                    else -> 1f
                }
                Box(
                    modifier = Modifier
                        .offset(x = posX - 54.dp, y = posY - 54.dp)
                        .size(108.dp)
                        .graphicsLayer {
                            scaleX = nodeScale
                            scaleY = nodeScale
                        }
                        .clip(CircleShape)
                        .clickable(enabled = !isLocked) { onChapterSelected(level.id) }
                        .background(
                            if (isLocked) Brush.verticalGradient(listOf(Color.Gray, Color.DarkGray))
                            else Brush.radialGradient(listOf(nodeColor.copy(alpha = 0.85f), nodeColor))
                        )
                        .border(
                            4.dp,
                            when {
                                isLocked -> Color.LightGray
                                isCompleted && starsEarned == 3 -> Color(0xFFFFD700)
                                isCompleted -> Color(0xFFB2FF59)
                                else -> Color(0xFFFFF59D)
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (!isLocked) {
                            Image(
                                painter = painterResource(id = level.world.arcadeIconRes()),
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                        if (isLocked) {
                            Icon(Icons.Default.Lock, null, tint = Color.White.copy(alpha = 0.55f), modifier = Modifier.size(36.dp))
                        } else {
                            Text(
                                text = "${level.id}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                style = TextStyle(shadow = Shadow(Color.Black.copy(alpha = 0.5f), blurRadius = 8f))
                            )
                        }
                        if (!isLocked) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(3) { idx ->
                                    val filled = idx < starsEarned
                                    Canvas(modifier = Modifier.size(10.dp)) {
                                        drawCircle(
                                            color = if (filled) Color(0xFFFFD700) else Color.White.copy(alpha = 0.25f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Level Name
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .offset(x = posX - 110.dp, y = posY + 62.dp)
                        .width(220.dp)
                ) {
                    Text(
                        text = stringResource(level.nameRes),
                        color = if (isLocked) Color.Gray else Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 8f))
                    )
                    if (!isLocked) {
                        Text(
                            text = stringResource(R.string.lbl_tap_to_play_level),
                            color = Color(0xFFFFF59D),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp),
                            style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 6f))
                        )
                    }
                }

                // Current Player Indicator
                if (level.id == unlockedChapter) {
                    Image(
                        painter = painterResource(id = charRes),
                        contentDescription = null,
                        modifier = Modifier
                            .offset(x = posX - 58.dp, y = posY - 152.dp + charFloatY.dp)
                            .size(116.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

        // Header Overlay (Her zaman en üstte durur)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .safeDrawingPadding()
                .align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BtnBack(onClick = onNavigateBack)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(R.string.title_chapter_select),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 16f))
            )
        }
    }
}
