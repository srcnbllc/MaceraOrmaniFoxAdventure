package com.zekaoformani.macera.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zekaoformani.macera.R
import com.zekaoformani.macera.data.SoundManager
import com.zekaoformani.macera.data.models.PlayStyle
import com.zekaoformani.macera.data.models.GameState
import com.zekaoformani.macera.data.models.characters
import com.zekaoformani.macera.ui.components.HUDLayer
import com.zekaoformani.macera.ui.components.RewardScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

data class ScorePopup(val id: Int, val screenX: Float, val screenY: Float, val text: String, val born: Long = System.currentTimeMillis())
data class PipePair(
    val id: Int,
    val screenLeft: Float,
    val gapTopY: Float,
    val gapBotY: Float,
    val pipeWidth: Float,
    val hasFruit: Boolean = true,
    val fruitCollected: Boolean = false,
    val initialGapCenterY: Float = 0f
)
data class DustParticle(val id: Int, val startX: Float, val startY: Float, val born: Long = System.currentTimeMillis())
data class GroundObstacle(
    val id: Int,
    val x: Float,
    val width: Float,
    val height: Float,
    val hasCollectible: Boolean,
    val collectibleCollected: Boolean = false
)

@Composable
fun GameScreen(
    characterId: Int = 1,
    chapterId: Int = 1,
    onNavigateBack: () -> Unit = {},
    onLevelCompleted: (levelId: Int, score: Int, stars: Int) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    val haptic = LocalHapticFeedback.current

    val levelData = com.zekaoformani.macera.data.models.levels.find { it.id == chapterId } ?: com.zekaoformani.macera.data.models.levels[0]
    val heroData = com.zekaoformani.macera.data.models.characters.find { it.id == characterId } ?: com.zekaoformani.macera.data.models.characters[0]

    DisposableEffect(Unit) { onDispose { soundManager.release() } }

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenW = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenH = with(density) { configuration.screenHeightDp.dp.toPx() }

    val d = density.density
    val groundH = screenH * 0.18f
    val groundTop = screenH - groundH
    val spriteSize = with(density) { 90.dp.toPx() }
    val pipeW = with(density) { 85.dp.toPx() }
    val gapH = (spriteSize * 3.2f) * levelData.gapSizeScale

    val speedMul = 0.8f + (heroData.speedStars * 0.1f)
    val jumpMul = 0.8f + (heroData.jumpStars * 0.05f)
    
    val gravity = 0.45f * d
    val jumpVel = -11.0f * jumpMul * d
    val gameSpeed = 3.8f * speedMul * levelData.speedScale * d
    val levelDist = levelData.totalDistance * d
    var charX by remember { mutableFloatStateOf(screenW * 0.22f) }
    val minCharX = screenW * 0.08f
    val maxCharX = screenW * 0.55f

    var gameState by remember { mutableStateOf(GameState.READY) }
    var isPaused by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var fruits by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(heroData.initialLives) }
    
    var charY by remember { mutableFloatStateOf(screenH * 0.4f) }
    var charVelY by remember { mutableFloatStateOf(0f) }

    var pipes by remember { mutableStateOf<List<PipePair>>(emptyList()) }
    var groundObstacles by remember { mutableStateOf<List<GroundObstacle>>(emptyList()) }
    var popups by remember { mutableStateOf<List<ScorePopup>>(emptyList()) }
    var dusts by remember { mutableStateOf<List<DustParticle>>(emptyList()) }
    
    var pipeIdCtr by remember { mutableIntStateOf(0) }
    var groundObsIdCtr by remember { mutableIntStateOf(0) }
    var dustIdCtr by remember { mutableIntStateOf(0) }
    
    var totalDist by remember { mutableFloatStateOf(0f) }
    var distCtr by remember { mutableFloatStateOf(0f) }
    var bgOffset by remember { mutableFloatStateOf(0f) }
    var invFrames by remember { mutableIntStateOf(0) }
    var frameCtr by remember { mutableIntStateOf(0) }

    var moveLeftHeld by remember { mutableStateOf(false) }
    var moveRightHeld by remember { mutableStateOf(false) }

    val bgRes = levelData.backgroundRes
    val charRes = heroData.imageRes

    val infiniteTransition = rememberInfiniteTransition(label = "gameAnims")
    val readyBobOffsetY by infiniteTransition.animateFloat(
        initialValue = -15f, targetValue = 15f,
        animationSpec = infiniteRepeatable(animation = tween(1000, easing = EaseInOutSine), repeatMode = RepeatMode.Reverse),
        label = "readyBob"
    )
    val handScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(animation = tween(600, easing = EaseInOutSine), repeatMode = RepeatMode.Reverse),
        label = "handPulse"
    )

    LaunchedEffect(gameState, isPaused) {
        if (gameState != GameState.PLAYING || isPaused) return@LaunchedEffect
        while (isActive) {
            delay(16L)
            frameCtr++

            val playStyle = levelData.playStyle
            val moveSpeed = (9.5f * d) * (0.75f + heroData.speedStars * 0.08f)
            if (moveLeftHeld && !moveRightHeld) charX = (charX - moveSpeed).coerceAtLeast(minCharX)
            if (moveRightHeld && !moveLeftHeld) charX = (charX + moveSpeed).coerceAtMost(maxCharX)

            // Physics differs by play style
            val groundY = groundTop - spriteSize - 6f
            if (playStyle == PlayStyle.GROUND) {
                // Jump arc, land on ground
                charVelY += gravity
                charY += charVelY
                if (charY >= groundY) {
                    charY = groundY
                    charVelY = 0f
                }
            } else {
                // AIR / MIXED: flappy-style
                charVelY += gravity
                charY += charVelY
            }

            if (charY + spriteSize >= groundTop || charY < 0f) {
                if (invFrames == 0) {
                    lives--
                    invFrames = (78 + heroData.durabilityStars * 10)
                    soundManager.playGameOver()
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (lives <= 0) { gameState = GameState.GAME_OVER }
                    if (levelData.playStyle == PlayStyle.GROUND) {
                        charY = groundTop - spriteSize - 6f
                        charVelY = 0f
                    } else {
                        charY = if (charY < 0f) 5f else groundTop - spriteSize - 6f
                        charVelY = 0f
                    }
                }
            }

            if (invFrames > 0) invFrames--

            bgOffset -= gameSpeed * 0.4f
            if (bgOffset <= -screenW) bgOffset += screenW
            totalDist += gameSpeed; distCtr += gameSpeed
            
            if (totalDist >= levelDist) { gameState = GameState.LEVEL_COMPLETE }

            if (levelData.playStyle == PlayStyle.GROUND) {
                // Ground obstacles
                val updated = groundObstacles.map { it.copy(x = it.x - gameSpeed) }.toMutableList()
                if (distCtr > levelData.obstacleFrequency * d) {
                    distCtr = 0f
                    val h = (45f * d) + (Math.random() * (55f * d)).toFloat()
                    val w = (70f * d) + (Math.random() * (55f * d)).toFloat()
                    updated.add(
                        GroundObstacle(
                            id = groundObsIdCtr++,
                            x = screenW + 40f,
                            width = w,
                            height = h,
                            hasCollectible = Math.random() > 0.25
                        )
                    )
                }
                updated.removeAll { it.x + it.width < -120f }

                groundObstacles = updated.map { o ->
                    val inX = (charX + spriteSize * 0.7f) > o.x && (charX + spriteSize * 0.3f) < (o.x + o.width)
                    val obstacleTopY = groundTop - o.height
                    if (inX) {
                        // collectible
                        if (o.hasCollectible && !o.collectibleCollected) {
                            val cY = obstacleTopY - 55f
                            if (charY + spriteSize * 0.7f > cY - 35f && charY + spriteSize * 0.3f < cY + 35f) {
                                score += 150; fruits++
                                soundManager.playCollect()
                                popups = popups + ScorePopup(frameCtr, o.x, cY, "+150")
                                return@map o.copy(collectibleCollected = true)
                            }
                        }
                        // hit obstacle body (if not above it)
                        val onOrBelowTop = (charY + spriteSize * 0.92f) > obstacleTopY
                        if (onOrBelowTop && invFrames == 0) {
                            lives--
                            invFrames = (78 + heroData.durabilityStars * 10)
                            soundManager.playGameOver()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (lives <= 0) { gameState = GameState.GAME_OVER }
                        }
                    }
                    o
                }
            } else {
                // Air pipes (AIR / MIXED)
                val updatedPipes = pipes.map { it.copy(screenLeft = it.screenLeft - gameSpeed) }.toMutableList()
                if (distCtr > levelData.obstacleFrequency * d) {
                    distCtr = 0f
                    val gapCenterY = (screenH * 0.25f) + (Math.random() * (screenH * 0.45f)).toFloat()
                    updatedPipes.add(PipePair(
                        id = pipeIdCtr++,
                        screenLeft = screenW + 20f,
                        gapTopY = gapCenterY - gapH / 2f,
                        gapBotY = gapCenterY + gapH / 2f,
                        pipeWidth = pipeW,
                        hasFruit = Math.random() > 0.35
                    ))
                }
                updatedPipes.removeAll { it.screenLeft + it.pipeWidth < -100f }

                pipes = updatedPipes.map { p ->
                    val pRight = p.screenLeft + p.pipeWidth
                    val inX = (charX + spriteSize * 0.7f) > p.screenLeft && (charX + spriteSize * 0.3f) < pRight
                    if (inX) {
                        if (p.hasFruit && !p.fruitCollected) {
                            val fruitCY = (p.gapTopY + p.gapBotY) / 2f
                            if (charY + spriteSize * 0.7f > fruitCY - 40f && charY + spriteSize * 0.3f < fruitCY + 40f) {
                                score += 150; fruits++
                                soundManager.playCollect()
                                popups = popups + ScorePopup(frameCtr, p.screenLeft, fruitCY, "+150")
                                return@map p.copy(fruitCollected = true)
                            }
                        }
                        if ((charY + spriteSize * 0.25f < p.gapTopY || charY + spriteSize * 0.75f > p.gapBotY) && invFrames == 0) {
                            lives--
                            invFrames = (78 + heroData.durabilityStars * 10)
                            soundManager.playGameOver()
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            if (lives <= 0) { gameState = GameState.GAME_OVER }
                        }
                    }
                    p
                }
            }

            popups = popups.filter { frameCtr - it.id < 60 }
            dusts = dusts.filter { System.currentTimeMillis() - it.born < 600 }
        }
    }

    val charAlpha = if (invFrames > 0 && (invFrames / 6) % 2 == 0) 0.3f else 1f
    val smoothTilt by animateFloatAsState(targetValue = (charVelY * 2.2f).coerceIn(-45f, 45f), animationSpec = tween(80), label = "tilt")

    val tapEnabled = levelData.playStyle != PlayStyle.GROUND
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (tapEnabled) {
                    Modifier.pointerInput(gameState, isPaused) {
                        detectTapGestures {
                            if (isPaused) return@detectTapGestures
                            if (gameState == GameState.READY || gameState == GameState.PLAYING) {
                                if (gameState == GameState.READY) gameState = GameState.PLAYING
                                charVelY = jumpVel
                                dusts = dusts + DustParticle(dustIdCtr++, charX + spriteSize / 2f, charY + spriteSize)
                                soundManager.playJump()
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        }
                    }
                } else Modifier
            )
    ) {
        Image(painter = painterResource(bgRes), contentDescription = null, contentScale = ContentScale.FillBounds, modifier = Modifier.fillMaxSize().graphicsLayer { translationX = bgOffset })
        Image(painter = painterResource(bgRes), contentDescription = null, contentScale = ContentScale.FillBounds, modifier = Modifier.fillMaxSize().graphicsLayer { translationX = bgOffset + screenW })

        val isBeach = levelData.world == com.zekaoformani.macera.data.models.WorldType.BEACH
        val isSky = levelData.world == com.zekaoformani.macera.data.models.WorldType.SKY
        val isCrystal = levelData.world == com.zekaoformani.macera.data.models.WorldType.CRYSTAL

        Canvas(modifier = Modifier.fillMaxSize()) {
            pipes.forEach { p ->
                val left  = p.screenLeft
                val right = left + p.pipeWidth

                val vineColor1 = when {
                    isBeach -> Color(0xFF7CB342)
                    isSky -> Color(0xFF64B5F6)
                    isCrystal -> Color(0xFF9575CD)
                    else -> Color(0xFF1B5E20)
                }
                val vineColor2 = when {
                    isBeach -> Color(0xFF558B2F)
                    isSky -> Color(0xFF2196F3)
                    isCrystal -> Color(0xFF512DA8)
                    else -> Color(0xFF33691E)
                }

                val obstacleColor1 = when {
                    isBeach -> Color(0xFFFFD54F) 
                    isSky -> Color(0xFFE3F2FD)
                    isCrystal -> Color(0xFFE1BEE7)
                    else -> Color(0xFF5D4037)
                }
                val obstacleColor2 = when {
                    isBeach -> Color(0xFFFFB300)
                    isSky -> Color(0xFF90CAF9)
                    isCrystal -> Color(0xFF9575CD)
                    else -> Color(0xFF795548)
                }
                
                val topH = p.gapTopY
                if (topH > 0f) {
                    val path = Path().apply {
                        moveTo(left, 0f)
                        lineTo(right, 0f)
                        cubicTo(right, topH * 0.5f, right - 10f, topH * 0.8f, right, topH)
                        cubicTo(left + 10f, topH * 0.8f, left, topH * 0.5f, left, 0f)
                    }
                    drawPath(path = path, brush = Brush.verticalGradient(listOf(vineColor2, vineColor1)))
                    
                    val capHeight = 35f
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(vineColor1, vineColor2)),
                        topLeft = Offset(left - 12f, topH - capHeight),
                        size = Size(p.pipeWidth + 24f, capHeight),
                        cornerRadius = CornerRadius(16f, 16f)
                    )
                }

                val botStart = p.gapBotY
                val botH     = groundTop - botStart
                if (botH > 0f) {
                    val stumpPath = Path().apply {
                        moveTo(left + 5f, botStart + 30f)
                        lineTo(right - 5f, botStart + 30f)
                        lineTo(right + 5f, groundTop)
                        lineTo(left - 5f, groundTop)
                        close()
                    }
                    drawPath(
                        path = stumpPath,
                        brush = Brush.horizontalGradient(listOf(obstacleColor1, obstacleColor2, obstacleColor1), startX = left, endX = right)
                    )
                    
                    val capHeight = 35f
                    drawRoundRect(
                        brush = Brush.verticalGradient(listOf(obstacleColor1, obstacleColor2)),
                        topLeft = Offset(left - 12f, botStart),
                        size = Size(p.pipeWidth + 24f, capHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )
                }

                if (p.hasFruit && !p.fruitCollected) {
                    val fruitCX = left + p.pipeWidth / 2f
                    val fruitCY = (p.gapTopY + p.gapBotY) / 2f
                    val r = 24f
                    drawCircle(Brush.radialGradient(
                        listOf(Color(0xFFFFFF88), Color(0xFFFFD700), Color(0xFFFF9800)),
                        center = Offset(fruitCX, fruitCY), radius = r * 2f
                    ), radius = r * 1.5f, center = Offset(fruitCX, fruitCY), alpha = 0.3f)
                    drawCircle(Brush.radialGradient(
                        listOf(Color(0xFFFFFF44), Color(0xFFFFD700), Color(0xFFFF8800)),
                        center = Offset(fruitCX - 5f, fruitCY - 5f), radius = r
                    ), radius = r, center = Offset(fruitCX, fruitCY))
                }
            }

            // Ground obstacles (GROUND play style)
            if (levelData.playStyle == PlayStyle.GROUND) {
                groundObstacles.forEach { o ->
                    val topY = groundTop - o.height
                    drawRoundRect(
                        color = if (isBeach) Color(0xFF8D6E63) else if (isCrystal) Color(0xFF7E57C2) else Color(0xFF6D4C41),
                        topLeft = Offset(o.x, topY),
                        size = Size(o.width, o.height),
                        cornerRadius = CornerRadius(16f, 16f),
                        alpha = 0.95f
                    )
                    if (o.hasCollectible && !o.collectibleCollected) {
                        val cx = o.x + o.width / 2f
                        val cy = topY - 55f
                        drawCircle(Color(0xFFFFD700), radius = 18f, center = Offset(cx, cy), alpha = 0.85f)
                        drawCircle(Color(0xFFFFFFFF), radius = 7f, center = Offset(cx - 6f, cy - 6f), alpha = 0.55f)
                    }
                }
            }

            val groundColor1 = if (isBeach) Color(0xFFFFA726) else Color(0xFF1B5E20)
            val groundColor2 = if (isBeach) Color(0xFFFF9800) else Color(0xFF0D2B00)
            drawRect(
                Brush.verticalGradient(listOf(groundColor1, groundColor2), startY = groundTop, endY = size.height),
                topLeft = Offset(0f, groundTop), size = Size(size.width, groundH)
            )
            drawLine(Color.White.copy(0.3f), Offset(0f, groundTop), Offset(size.width, groundTop), strokeWidth = 5f)

            dusts.forEach { dp ->
                val age = (System.currentTimeMillis() - dp.born) / 600f
                drawCircle(Color.White.copy(1f - age), radius = 10f * (1f + age), center = Offset(dp.startX, dp.startY))
            }
        }

        Image(
            painter = painterResource(charRes), contentDescription = null,
            modifier = Modifier
                .offset(x = with(density) { charX.toDp() }, y = with(density) { (charY + if (gameState == GameState.READY) readyBobOffsetY else 0f).toDp() })
                .size(with(density) { spriteSize.toDp() })
                .graphicsLayer { rotationZ = smoothTilt; alpha = charAlpha }
        )

        // 4. Hazır Mısın Ekranı (READY)
        if (gameState == GameState.READY) {
            Column(
                modifier = Modifier.fillMaxSize().padding(bottom = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.tutorial_tap_action),
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 24f))
                )
                Spacer(modifier = Modifier.height(40.dp))
                Icon(
                    Icons.Default.TouchApp, 
                    contentDescription = null, 
                    tint = Color.White, 
                    modifier = Modifier.size(80.dp).graphicsLayer { scaleX = handScale; scaleY = handScale }
                )
            }
        }

        // 5. HUD Katmanı
        HUDLayer(
            title = stringResource(levelData.nameRes),
            score = score,
            fruits = fruits,
            lives = lives,
            collectibleMax = levelData.starTargets.star3,
            isPaused = isPaused,
            onNavigateBack = onNavigateBack,
            onJump = {
                if (isPaused) return@HUDLayer
                if (gameState == GameState.READY) gameState = GameState.PLAYING
                if (gameState == GameState.PLAYING) {
                    charVelY = jumpVel
                    dusts = dusts + DustParticle(dustIdCtr++, charX + spriteSize / 2f, charY + spriteSize)
                    soundManager.playJump()
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            },
            onPauseToggle = { isPaused = !isPaused },
            onMoveLeft = { moveLeftHeld = it },
            onMoveRight = { moveRightHeld = it },
            showDpad = levelData.playStyle != PlayStyle.AIR
        )

        // 6. Skor Popupları
        popups.forEach { p ->
            Text(
                p.text, 
                color = Color.Yellow, 
                fontSize = 24.sp, 
                fontWeight = FontWeight.Bold,
                style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 8f)),
                modifier = Modifier.offset(x = with(density) { p.screenX.toDp() }, y = with(density) { (p.screenY - 50f).toDp() })
            )
        }

        // 7. Duraklatma Ekranı (PAUSE)
        if (isPaused) {
            Box(
                Modifier.fillMaxSize().background(Color.Black.copy(0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.pause_title), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = { isPaused = false }) {
                        Text(stringResource(R.string.btn_resume))
                    }
                }
            }
        }

        // 8. Oyun Sonu & Bölüm Sonu Ekranları
        if (gameState == GameState.GAME_OVER) {
            RewardScreen(
                score = score,
                stars = 0,
                isVictory = false,
                characterId = characterId,
                chapterName = stringResource(R.string.hud_chapter, chapterId),
                onNext = { /* N/A */ },
                onMap = onNavigateBack,
                onReplay = { 
                    pipes = emptyList(); score = 0; fruits = 0; totalDist = 0f; distCtr = 0f
                    lives = heroData.initialLives; charY = screenH * 0.4f; charVelY = 0f
                    gameState = GameState.READY 
                }
            )
        }

        if (gameState == GameState.LEVEL_COMPLETE) {
            val targets = levelData.starTargets
            val starsEarned = when {
                fruits >= targets.star3 -> 3
                fruits >= targets.star2 -> 2
                fruits >= targets.star1 -> 1
                else -> 0
            }
            RewardScreen(
                score = score,
                stars = starsEarned,
                isVictory = true,
                characterId = characterId,
                chapterName = stringResource(levelData.nameRes),
                onNext = { onLevelCompleted(chapterId, score, starsEarned) },
                onMap = onNavigateBack,
                onReplay = { 
                    pipes = emptyList(); score = 0; fruits = 0; totalDist = 0f; distCtr = 0f
                    lives = heroData.initialLives; charY = screenH * 0.4f; charVelY = 0f
                    gameState = GameState.READY 
                }
            )
        }
    }
}
