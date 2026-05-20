package com.zekaoformani.macera.ui.components

import androidx.compose.animation.core.*
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.zekaoformani.macera.data.models.characters
import kotlinx.coroutines.delay
import kotlin.random.Random
import maceraormanifoxadventure.shared.generated.resources.Res
import maceraormanifoxadventure.shared.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.DrawableResource

data class ConfettiParticle(
    val x: Float, val speed: Float, val size: Float, val color: Color,
    val rotation: Float, val rotSpeed: Float, val shape: Int
)

@Composable
fun RewardScreen(
    score: Int,
    stars: Int,
    isVictory: Boolean = true,
    characterId: Int = 1,
    chapterName: String = "",
    onNext: () -> Unit,
    onMap: () -> Unit,
    onReplay: () -> Unit
) {
    // Karakter görselini alırken Res yapısını kullanıyoruz
    val charRes = characters.find { it.id == characterId }?.imageRes ?: Res.drawable.fox
    val haptic = LocalHapticFeedback.current

    // Sequential Star Pops (Sadece galibiyette)
    var star1Visible by remember { mutableStateOf(false) }
    var star2Visible by remember { mutableStateOf(false) }
    var star3Visible by remember { mutableStateOf(false) }

    LaunchedEffect(isVictory) {
        if (!isVictory) return@LaunchedEffect
        delay(400)
        if (stars >= 1) { star1Visible = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
        delay(400)
        if (stars >= 2) { star2Visible = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
        delay(400)
        if (stars >= 3) { star3Visible = true; haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
    }

    Dialog(
        onDismissRequest = onMap,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F172A).copy(alpha = 0.90f))
        ) {
            if (isVictory) {
                GodRays()
                ConfettiAnimation()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Başlık
                Text(
                    text = if (isVictory) stringResource(Res.string.victory_title)
                    else stringResource(Res.string.game_over_title),
                    color = if (isVictory) Color(0xFFFFD700) else Color(0xFFFF5252),
                    fontSize = if (isVictory) 44.sp else 48.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    style = TextStyle(shadow = Shadow(if (isVictory) Color(0xFFD84315) else Color.Black, blurRadius = 24f))
                )

                if (isVictory) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = chapterName,
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.height(100.dp)
                    ) {
                        AnimatedStar(visible = star1Visible, size = 64.dp, rotation = -15f)
                        Spacer(modifier = Modifier.width(8.dp))
                        AnimatedStar(visible = star2Visible, size = 86.dp, rotation = 0f, pulse = true)
                        Spacer(modifier = Modifier.width(8.dp))
                        AnimatedStar(visible = star3Visible, size = 64.dp, rotation = 15f)
                    }
                } else {
                    Text(
                        stringResource(Res.string.game_over_subtitle),
                        color = Color.White.copy(0.7f),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (chapterName.isNotEmpty()) {
                        Text(
                            text = chapterName,
                            color = Color.White.copy(alpha = 0.4f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }

                Spacer(modifier = Modifier.weight(0.4f))

                // Karakter Animasyonu
                val infiniteTransition = rememberInfiniteTransition(label = "rewardCharAnim")
                val charY by infiniteTransition.animateFloat(
                    initialValue = 0f, targetValue = -25f,
                    animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOutSine), RepeatMode.Reverse),
                    label = "charFloat"
                )

                AsyncImage(
                    model = charRes,
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .offset(y = charY.dp)
                        .graphicsLayer { alpha = if (isVictory) 1f else 0.7f },
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.weight(1f))

                // Skor Kartı
                Surface(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, if (isVictory) Color(0xFF38BDF8) else Color(0xFFEF4444)),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(Res.string.lbl_total_score),
                            color = Color.White.copy(0.5f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$score",
                            color = Color.White,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Butonlar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BubbleButton(
                        icon = Icons.Default.Refresh,
                        color = Color(0xFFF43F5E),
                        text = stringResource(Res.string.btn_retry),
                        onClick = onReplay
                    )
                    BubbleButton(
                        icon = Icons.Default.Place,
                        color = Color(0xFF8B5CF6),
                        text = stringResource(Res.string.btn_map),
                        onClick = onMap,
                        size = 80.dp
                    )
                    if (isVictory) {
                        BubbleButton(
                            icon = Icons.AutoMirrored.Filled.ArrowForward,
                            color = Color(0xFF10B981),
                            text = stringResource(Res.string.btn_next_level),
                            onClick = onNext
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun AnimatedStar(visible: Boolean, size: androidx.compose.ui.unit.Dp, rotation: Float, pulse: Boolean = false) {
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "starScale"
    )

    val pulseScale = if (pulse && visible) {
        val transition = rememberInfiniteTransition(label = "pulse")
        transition.animateFloat(
            initialValue = 1f, targetValue = 1.15f,
            animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
            label = "p"
        ).value
    } else 1f

    Icon(
        imageVector = Icons.Default.Star,
        contentDescription = null,
        tint = if (visible) Color(0xFFFFD700) else Color(0xFF475569),
        modifier = Modifier
            .size(size)
            .graphicsLayer(
                rotationZ = rotation,
                scaleX = scale * pulseScale,
                scaleY = scale * pulseScale
            )
    )
}

@Composable
fun BubbleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    text: String,
    size: androidx.compose.ui.unit.Dp = 72.dp,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "btnScale"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(size)
                .graphicsLayer { scaleX = scale; scaleY = scale }
                .clip(CircleShape)
                .background(Brush.radialGradient(listOf(color.copy(alpha=0.8f), color)))
                .border(3.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                .clickable(interactionSource = interactionSource, indication = null) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = text, tint = Color.White, modifier = Modifier.size(size * 0.45f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.widthIn(max = size * 1.5f))
    }
}

@Composable
fun GodRays() {
    val infiniteTransition = rememberInfiniteTransition(label = "godRays")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing)),
        label = "rayRot"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2
        val cy = size.height / 2
        rotate(rotation, Offset(cx, cy)) {
            val rayCount = 12
            val angleStep = 360f / rayCount
            for (i in 0 until rayCount) {
                rotate(i * angleStep, Offset(cx, cy)) {
                    drawPath(
                        path = androidx.compose.ui.graphics.Path().apply {
                            moveTo(cx, cy)
                            lineTo(cx - 100f, -size.height)
                            lineTo(cx + 100f, -size.height)
                            close()
                        },
                        color = Color(0xFFFFD54F).copy(alpha = 0.15f)
                    )
                }
            }
        }
    }
}

@Composable
fun ConfettiAnimation() {
    val confettiColors = listOf(Color(0xFFFF4B4B), Color(0xFFFF8C00), Color(0xFFFFD700), Color(0xFF4CAF50), Color(0xFF2196F3))

    val particles = remember {
        List(40) {
            ConfettiParticle(
                x = Random.nextFloat(), speed = 0.4f + Random.nextFloat() * 0.8f,
                size = 8f + Random.nextFloat() * 12f, color = confettiColors[Random.nextInt(confettiColors.size)],
                rotation = Random.nextFloat() * 360f, rotSpeed = -3f + Random.nextFloat() * 6f, shape = Random.nextInt(3)
            )
        }
    }

    val transition = rememberInfiniteTransition(label = "confetti")
    val progress by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "cP"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { p ->
            val phase = (p.x * 0.5f + progress) % 1f
            val yPos = phase * (size.height + 100f) - 50f
            val xPos = p.x * size.width + kotlin.math.sin((progress + p.rotation) * 6.28f) * 50f
            val rot = p.rotation + progress * p.rotSpeed * 360f

            rotate(rot, Offset(xPos, yPos)) {
                if (p.shape == 0) drawRect(p.color.copy(alpha=0.8f), topLeft = Offset(xPos, yPos), size = Size(p.size, p.size*2))
                else drawCircle(p.color.copy(alpha=0.8f), radius = p.size, center = Offset(xPos, yPos))
            }
        }
    }
}
