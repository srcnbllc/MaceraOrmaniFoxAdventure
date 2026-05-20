package com.zekaoformani.macera.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zekaoformani.macera.R

@Composable
fun HUDLayer(
    title: String,
    score: Int,
    fruits: Int,
    lives: Int,
    collectibleMax: Int,
    isPaused: Boolean = false,
    onNavigateBack: () -> Unit,
    onJump: () -> Unit,
    onPauseToggle: () -> Unit = {},
    // D-Pad kontrolleri için
    onMoveLeft: (Boolean) -> Unit = {},
    onMoveRight: (Boolean) -> Unit = {},
    showDpad: Boolean = true
) {
    val haptic = LocalHapticFeedback.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Top HUD Gradient Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
        )

        // Top HUD Elements
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .safeDrawingPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol Üst: Bölüm Adı ve Pause
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onPauseToggle()
                    },
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(Color.White.copy(alpha=0.15f))
                ) {
                    Icon(Icons.Default.Pause, contentDescription = stringResource(R.string.cd_pause_btn), tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                // Bölüm Numarası/Adı (Genel Format)
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(shadow = Shadow(Color.Black.copy(alpha=0.5f), blurRadius = 4f))
                )
            }

            // Orta Üst: Canlar (Kalpler)
            StatCapsule(icon = Icons.Default.Favorite, value = lives.toString(), color = Color(0xFFFF4B4B))

            // Sağ Üst: Toplanan Obje
            val objIcon = Icons.Default.Star
            StatCapsule(icon = objIcon, value = "$fruits/$collectibleMax", color = Color(0xFFFFD700))
        }

        // --- CONTROLS LAYER ---
        if (!isPaused) {
            if (showDpad) {
                // D-Pad Left/Right (Bottom Left)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Arrow
                    val leftInteraction = remember { MutableInteractionSource() }
                    val isLeftPressed by leftInteraction.collectIsPressedAsState()
                    val leftScale by animateFloatAsState(targetValue = if (isLeftPressed) 0.85f else 1f, animationSpec = spring(stiffness = Spring.StiffnessLow), label = "lScale")
                    
                    LaunchedEffect(isLeftPressed) { onMoveLeft(isLeftPressed) }

                    Box(
                        modifier = Modifier
                            .size(72.dp) // Touch target büyütüldü
                            .graphicsLayer { scaleX = leftScale; scaleY = leftScale }
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f)) // Opaklık %40 şeffaf overlay
                            .border(2.dp, Color.White.copy(alpha=0.2f), CircleShape)
                            .clickable(interactionSource = leftInteraction, indication = null) { 
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) 
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = stringResource(R.string.cd_left_btn), tint = Color.White.copy(alpha=0.8f), modifier = Modifier.size(48.dp))
                    }

                    // Right Arrow
                    val rightInteraction = remember { MutableInteractionSource() }
                    val isRightPressed by rightInteraction.collectIsPressedAsState()
                    val rightScale by animateFloatAsState(targetValue = if (isRightPressed) 0.85f else 1f, animationSpec = spring(stiffness = Spring.StiffnessLow), label = "rScale")

                    LaunchedEffect(isRightPressed) { onMoveRight(isRightPressed) }

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .graphicsLayer { scaleX = rightScale; scaleY = rightScale }
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                            .border(2.dp, Color.White.copy(alpha=0.2f), CircleShape)
                            .clickable(interactionSource = rightInteraction, indication = null) { 
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) 
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = stringResource(R.string.cd_right_btn), tint = Color.White.copy(alpha=0.8f), modifier = Modifier.size(48.dp))
                    }
                }
            }

            // Jump Button (Bottom Right)
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val jumpScale by animateFloatAsState(targetValue = if (isPressed) 0.85f else 1f, animationSpec = spring(stiffness = Spring.StiffnessLow), label = "jsScale")
            
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp)
                    .size(88.dp) // Zıplama en büyüğü
                    .graphicsLayer { scaleX = jumpScale; scaleY = jumpScale }
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha=0.4f))
                    .border(2.dp, Color.White.copy(alpha=0.2f), CircleShape)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { 
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onJump() 
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.cd_jump_btn),
                    tint = Color.White.copy(alpha=0.8f),
                    modifier = Modifier.size(56.dp)
                )
            }
        }
    }
}

@Composable
fun StatCapsule(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, color: Color) {
    Surface(
        color = Color.Black.copy(alpha = 0.5f), // Koyu ve okunaklı
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, color.copy(alpha = 0.6f)), // Juicy border
        modifier = Modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold, // Daha dolgun font
                fontSize = 18.sp,
                style = androidx.compose.ui.text.TextStyle(
                    shadow = Shadow(Color.Black.copy(alpha = 0.5f), blurRadius = 4f)
                )
            )
        }
    }
}
