package com.zekaoformani.macera.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.zekaoformani.macera.data.GamePreferences
import com.zekaoformani.macera.data.SoundManager
import com.zekaoformani.macera.ui.components.HUDLayer
import com.zekaoformani.macera.ui.components.LeafParticleSystem
import com.zekaoformani.macera.ui.components.RewardScreen
import kotlinx.coroutines.delay
import maceraormanifoxadventure.shared.generated.resources.Res
import maceraormanifoxadventure.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import kotlin.math.*
import kotlin.random.Random

// --- OYUN SABİTLERİ ---
const val GRAVITY = 0.8f
const val JUMP_FORCE = -18f
const val GROUND_Y = 0.85f

@Composable
fun GameScreen(
    characterId: Int,
    chapterId: Int,
    onNavigateBack: () -> Unit,
    onLevelCompleted: (Int, Int, Int) -> Unit
) {
    val prefs = remember { GamePreferences() }
    val soundManager = remember { SoundManager.getInstance() }

    // Ekran Boyutları (Basitleştirilmiş)
    var screenWidth by remember { mutableFloatStateOf(1080f) }
    var screenHeight by remember { mutableFloatStateOf(1920f) }

    // Oyun Durumu
    var isPaused by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var isVictory by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }

    // Kahraman Özellikleri
    val tentLevel = prefs.getTentLevel()
    val initialShield = if (tentLevel >= 2) 2 else if (tentLevel == 1) 1 else 0
    var shieldCount by remember { mutableIntStateOf(initialShield) }
    val campfireLevel = prefs.getCampfireLevel()
    val dummyLevel = prefs.getDummyLevel()
    
    val magnetRadius = 150f + (dummyLevel * 40f)

    // Arka Plan Kaydırma
    val bgScrollX = rememberInfiniteTransition().animateFloat(
        initialValue = 0f, targetValue = -1000f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing)), label = ""
    )

    // Karakter Pozisyonu
    var charY by remember { mutableFloatStateOf(0.7f) }
    var charVelocityY by remember { mutableFloatStateOf(0f) }
    var isJumping by remember { mutableStateOf(false) }

    // Oyun Döngüsü
    LaunchedEffect(isPaused, isGameOver) {
        while (!isPaused && !isGameOver) {
            delay(16) // ~60 FPS
            if (isJumping) {
                charVelocityY += GRAVITY
                charY += charVelocityY / 1000f
                if (charY >= 0.7f) {
                    charY = 0.7f
                    isJumping = false
                    charVelocityY = 0f
                }
            }
            score += 1
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Arka Plan
        AsyncImage(
            model = Res.drawable.sonsuz_orman,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Karakter
        AsyncImage(
            model = when(characterId) {
                2 -> Res.drawable.monkey
                3 -> Res.drawable.tigger
                else -> Res.drawable.fox
            },
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomStart)
                .offset(x = 50.dp, y = (- (1.0f - charY) * 500).dp)
        )

        // HUD Katmanı
        HUDLayer(
            score = score,
            lives = lives,
            shieldCount = shieldCount,
            onPauseClick = { isPaused = true }
        )

        // Duraklatma Menüsü
        if (isPaused) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("DURAKLATILDI", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(onClick = { isPaused = false }) { Text("DEVAM ET") }
                    Button(onClick = onNavigateBack) { Text("ANA MENÜ") }
                }
            }
        }

        // Oyun Sonu / Zafer Ekranı
        if (isGameOver || isVictory) {
            RewardScreen(
                score = score,
                stars = if (score > 1000) 3 else if (score > 500) 2 else 1,
                isVictory = isVictory,
                characterId = characterId,
                onNext = { /* Yeniden başlat veya sonraki */ },
                onMap = onNavigateBack,
                onReplay = {
                    score = 0
                    lives = 3
                    isGameOver = false
                    isVictory = false
                }
            )
        }
    }
}
