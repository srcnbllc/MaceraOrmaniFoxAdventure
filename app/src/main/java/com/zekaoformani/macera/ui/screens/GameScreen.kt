package com.zekaoformani.macera.ui.screens

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.zekaoformani.macera.R
import com.zekaoformani.macera.data.DataManager
import com.zekaoformani.macera.data.SoundManager
import com.zekaoformani.macera.data.models.characters
import kotlinx.coroutines.delay
import kotlin.random.Random

enum class ObstacleType { GROUND }

data class Obstacle(
    val x: Float, val y: Float, val type: ObstacleType, val imageRes: Int, val width: Float, val height: Float
)

data class Coin(val x: Float, val y: Float, val width: Float = 45f, val height: Float = 45f)

data class Decoration(val x: Float, val y: Float, val speed: Float, val imageRes: Int)

@Composable
fun GameScreen(
    characterId: Int,
    chapterId: Int,
    onNavigateBack: () -> Unit,
    onLevelCompleted: (levelId: Int, scoreEarned: Int, starsEarned: Int) -> Unit
) {
    val context = LocalContext.current
    val dataManager = remember { DataManager(context) }
    val soundManager = remember { SoundManager(context) }
    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.toFloat()
    val spawnX = screenWidth + 200f

    val heroData = characters.find { it.id == characterId } ?: characters[0]

    val imageLoader = remember {
        ImageLoader.Builder(context).components {
            if (SDK_INT >= 28) add(ImageDecoderDecoder.Factory()) else add(GifDecoder.Factory())
        }.build()
    }

    var isPlaying by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var collectedCoins by remember { mutableIntStateOf(0) }

    val groundLevel = 0f
    var charY by remember { mutableFloatStateOf(groundLevel) }
    var velocityY by remember { mutableFloatStateOf(0f) }
    val gravity = 1.6f
    val baseJumpStrength = 22f + (heroData.jumpStars * 1.5f)

    var isSliding by remember { mutableStateOf(false) }
    var slideTimer by remember { mutableIntStateOf(0) }

    var obstacles by remember { mutableStateOf(listOf<Obstacle>()) }
    var coins by remember { mutableStateOf(listOf<Coin>()) }
    var birds by remember { mutableStateOf(listOf<Decoration>()) }

    // ARKA PLAN AYARLARI
    val bgImage = ImageBitmap.imageResource(id = R.drawable.sonsuz_orman)
    val density = context.resources.displayMetrics.density
    val rawImageWidthPx = bgImage.width.toFloat()
    val rawImageHeightPx = bgImage.height.toFloat()
    val screenHeightDp = configuration.screenHeightDp.toFloat()

    val scaleFactor = screenHeightDp / (rawImageHeightPx / density)
    val scaledImageWidthDp = (rawImageWidthPx / density) * scaleFactor

    var bgOffsetX by remember { mutableFloatStateOf(0f) }
    val baseSpeed = 5f + (chapterId * 0.5f)
    val bgSpeedFactor = 0.3f
    val speedIncreaseFactor = 1f + (score / 150f)
    val effectiveBgSpeed = (baseSpeed * bgSpeedFactor) * speedIncreaseFactor
    val effectiveGameSpeed = baseSpeed * speedIncreaseFactor

    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }

    LaunchedEffect(isPlaying, isGameOver) {
        if (isPlaying && !isGameOver) {
            soundManager.playBackgroundMusic()
        } else {
            soundManager.pauseBackgroundMusic()
        }
    }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(16)

            val loopWidth = scaledImageWidthDp * 2
            bgOffsetX -= effectiveBgSpeed
            if (bgOffsetX <= -loopWidth) {
                bgOffsetX += loopWidth
            }

            if (charY > groundLevel || velocityY > 0f) {
                charY += velocityY
                velocityY -= gravity
                if (charY <= groundLevel) {
                    charY = groundLevel
                    velocityY = 0f
                }
            }

            if (isSliding) {
                slideTimer--
                if (slideTimer <= 0) isSliding = false
            }

            val currentObstacles = obstacles.map { it.copy(x = it.x - effectiveGameSpeed) }.toMutableList()
            val currentCoins = coins.map { it.copy(x = it.x - effectiveGameSpeed) }.toMutableList()
            val currentBirds = birds.map { it.copy(x = it.x - (it.speed * speedIncreaseFactor)) }.toMutableList()

            currentObstacles.removeAll { obs ->
                val isPassed = obs.x < -200f
                if (isPassed) score += 10
                isPassed
            }
            currentCoins.removeAll { it.x < -100f }
            currentBirds.removeAll { it.x < -200f }

            if (Math.random() < 0.015) {
                currentBirds.add(Decoration(x = spawnX, y = Random.nextInt(180, 480).toFloat(),
                    speed = Random.nextDouble(1.8, 4.5).toFloat(), imageRes = R.drawable.obstacle_parrot))
            }

            if (currentObstacles.isEmpty() || currentObstacles.last().x < (spawnX - 600f)) {
                if (Math.random() < 0.02) {
                    val img = if (Math.random() < 0.5) R.drawable.obstacle_log else R.drawable.obstacle_rock
                    currentObstacles.add(Obstacle(x = spawnX, y = 0f, type = ObstacleType.GROUND, imageRes = img, width = 120f, height = 130f))

                    currentCoins.add(Coin(x = spawnX - 10f, y = 150f))
                    currentCoins.add(Coin(x = spawnX + 70f, y = 205f))
                    currentCoins.add(Coin(x = spawnX + 160f, y = 150f))
                }
            }

            obstacles = currentObstacles
            birds = currentBirds

            val charX = 100f
            val charW = if (isSliding) 140f else 120f
            val charH = if (isSliding) 80f else 150f

            val cLeft = charX + 45f
            val cRight = charX + charW - 45f
            val cBottom = charY + 10f
            val cTop = charY + (if (isSliding) charH - 10f else charH - 25f)

            val collectedThisFrame = currentCoins.filter { coin ->
                val oLeft = coin.x; val oRight = coin.x + coin.width
                val oBottom = coin.y; val oTop = coin.y + coin.height
                cLeft < oRight && cRight > oLeft && cBottom < oTop && cTop > oBottom
            }
            if (collectedThisFrame.isNotEmpty()) {
                collectedCoins += collectedThisFrame.size
                score += collectedThisFrame.size * 5
                currentCoins.removeAll(collectedThisFrame)
                soundManager.playCollect()
            }
            coins = currentCoins

            for (obs in obstacles) {
                val oLeft = obs.x + 35f; val oRight = obs.x + obs.width - 35f
                val oBottom = obs.y + 15f; val oTop = obs.y + 80f
                if (cLeft < oRight && cRight > oLeft && cBottom < oTop && cTop > oBottom) {
                    isPlaying = false
                    isGameOver = true
                    soundManager.pauseBackgroundMusic()
                    soundManager.playGameOver()
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount.y < -15f && isPlaying && !isGameOver && charY == groundLevel) {
                            velocityY = baseJumpStrength
                            isSliding = false
                            soundManager.playJump()
                        } else if (dragAmount.y > 15f && isPlaying && !isGameOver && charY == groundLevel && !isSliding) {
                            isSliding = true
                            slideTimer = 35
                        }
                    }
                }
        ) {

            Canvas(modifier = Modifier.fillMaxSize()) {
                val sX = (scaledImageWidthDp * density) / rawImageWidthPx
                val sY = (screenHeightDp * density) / rawImageHeightPx
                val offX = bgOffsetX * density
                withTransform({
                    translate(left = offX, top = 0f)
                    scale(scaleX = sX, scaleY = sY, pivot = Offset.Zero)
                }) {
                    drawImage(image = bgImage, topLeft = Offset.Zero)
                    withTransform({
                        translate(left = rawImageWidthPx * 2, top = 0f)
                        scale(scaleX = -1f, scaleY = 1f, pivot = Offset.Zero)
                    }) {
                        drawImage(image = bgImage, topLeft = Offset.Zero)
                    }
                    withTransform({ translate(left = rawImageWidthPx * 2, top = 0f) }) {
                        drawImage(image = bgImage, topLeft = Offset.Zero)
                    }
                }
            }

            val groundLineY = (screenHeightDp * 0.85f).dp
            val charPush = 10.dp
            val obsPush = 35.dp
            val coinPush = 25.dp

            Box(modifier = Modifier.fillMaxSize()) {
                birds.forEach { bird ->
                    val birdYPos = (screenHeightDp * 0.45f).dp - bird.y.dp
                    AsyncImage(model = bird.imageRes, imageLoader = imageLoader, contentDescription = null,
                        modifier = Modifier.offset(x = bird.x.dp, y = birdYPos).size(90.dp, 70.dp), contentScale = ContentScale.Fit)
                }

                coins.forEach { coin ->
                    Image(painter = painterResource(id = R.drawable.item_coin), contentDescription = "Coin",
                        modifier = Modifier.offset(x = coin.x.dp, y = groundLineY - coin.y.dp - coin.height.dp + coinPush).size(coin.width.dp, coin.height.dp))
                }

                obstacles.forEach { obs ->
                    val yPos = groundLineY - obs.y.dp - obs.height.dp + obsPush
                    Image(painter = painterResource(id = obs.imageRes), contentDescription = null,
                        modifier = Modifier.offset(x = obs.x.dp, y = yPos).size(obs.width.dp, obs.height.dp), contentScale = ContentScale.Fit)
                }

                val cW = if (isSliding) 140.dp else 120.dp
                val cH = if (isSliding) 80.dp else 150.dp
                AsyncImage(model = heroData.imageRes, imageLoader = imageLoader, contentDescription = "Hero",
                    modifier = Modifier.offset(x = 100.dp, y = groundLineY - charY.dp - cH + charPush).size(cW, cH),
                    contentScale = ContentScale.Fit, alignment = Alignment.BottomCenter)
            }

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp).safeDrawingPadding(), horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(
                    onClick = {
                        dataManager.addCoins(collectedCoins)
                        dataManager.saveHighScore(score)
                        isPlaying = false
                        onNavigateBack()
                    },
                    modifier = Modifier.background(Color.Black.copy(0.5f), CircleShape)
                ) {
                    Text("X", color = Color.White, fontWeight = FontWeight.Black)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("SKOR: $score", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black, style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 8f)))
                    Text("ALTIN: $collectedCoins", color = Color(0xFFFFD700), fontSize = 20.sp, fontWeight = FontWeight.Bold, style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 8f)))
                }
            }

            if (!isPlaying && !isGameOver && score == 0) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.6f)), contentAlignment = Alignment.Center) {
                    Button(onClick = { isPlaying = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                        Text("KOŞMAYA BAŞLA", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (isGameOver) {
                val isNewRecord = score > 0 && score > dataManager.getHighScore()

                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFF4E342E), Color(0xFF2E1A14))))
                            .border(4.dp, Color(0xFFFFB300), RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "YAKALANDIN!",
                            color = Color(0xFFFF5252),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 15f))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isNewRecord) {
                            Text(
                                text = "👑 YENİ REKOR! 👑",
                                color = Color(0xFF69F0AE),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 10f)),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        Text(
                            text = "SKOR: $score",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.Black.copy(0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.item_coin),
                                contentDescription = "Kazanılan Altın",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "+$collectedCoins",
                                color = Color(0xFFFFD700),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(55.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Brush.verticalGradient(listOf(Color(0xFF7F8C8D), Color(0xFF2C3E50))))
                                    .border(2.dp, Color.LightGray, RoundedCornerShape(16.dp))
                                    .clickable {
                                        dataManager.addCoins(collectedCoins)
                                        dataManager.saveHighScore(score)
                                        isPlaying = false
                                        onNavigateBack()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("MENÜ", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(55.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Brush.verticalGradient(listOf(Color(0xFF2ECC71), Color(0xFF27AE60))))
                                    .border(2.dp, Color.White, RoundedCornerShape(16.dp))
                                    .clickable {
                                        dataManager.addCoins(collectedCoins)
                                        dataManager.saveHighScore(score)

                                        score = 0
                                        collectedCoins = 0
                                        charY = groundLevel
                                        obstacles = emptyList()
                                        coins = emptyList()
                                        birds = emptyList()
                                        isGameOver = false
                                        isPlaying = true
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("TEKRAR", color = Color.White, fontWeight = FontWeight.Black, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}