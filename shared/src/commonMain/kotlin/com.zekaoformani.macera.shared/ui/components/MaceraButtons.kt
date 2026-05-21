package com.zekaoformani.macera.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.graphicsLayer
import com.zekaoformani.macera.ui.theme.LuminousPrimary
import com.zekaoformani.macera.ui.theme.LuminousPrimaryContainer
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun MaceraButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(Color(0xFFFF8C00), Color(0xFFFF6F00)),
    borderColor: Color = Color(0xFF8C4A00),
    icon: ImageVector? = null,
    iconRes: DrawableResource? = null,
    isCircular: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pulseScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "pulseAnim"
    )

    val shape = if (isCircular) CircleShape else RoundedCornerShape(32.dp)

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pulseScale
                scaleY = pulseScale
            }
            .offset(y = if (isPressed) 4.dp else 0.dp)
            .shadow(if (isPressed) 0.dp else 12.dp, shape, spotColor = colors.first().copy(alpha = 0.5f), ambientColor = colors.last().copy(alpha = 0.5f))
            .clip(shape)
            .background(Brush.verticalGradient(colors.map { it.copy(alpha = 0.85f) }))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (isPressed) 0.05f else 0.15f),
                shape = shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isPressed) 0.dp else 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = if (isCircular) 0.dp else 24.dp, vertical = if (isCircular) 0.dp else 16.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(if (isCircular) 28.dp else 24.dp)
                    )
                } else if (iconRes != null) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        modifier = Modifier.size(if (isCircular) 48.dp else 24.dp).clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
                
                if (text.isNotEmpty() && !isCircular) {
                    if (icon != null || iconRes != null) Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = text,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
fun BtnPlay(text: String = "OYNA", onClick: () -> Unit, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "playBtnGlow")
    val glowRadius by infiniteTransition.animateFloat(
        initialValue = 8f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAnim"
    )

    MaceraButton(
        text = text,
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .shadow(
                elevation = glowRadius.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = Color(0xFFFFD700),
                ambientColor = Color(0xFFFFC107)
            ),
        colors = listOf(LuminousPrimary, LuminousPrimaryContainer),
        borderColor = Color.Transparent
    )
}

@Composable
fun BtnBack(onClick: () -> Unit, modifier: Modifier = Modifier) {
    MaceraButton(
        text = "",
        onClick = onClick,
        modifier = modifier.size(56.dp),
        colors = listOf(Color(0xFF4CAF50), Color(0xFF388E3C)),
        borderColor = Color(0xFF1B5E20),
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        isCircular = true
    )
}

@Composable
fun BtnCancel(onClick: () -> Unit, modifier: Modifier = Modifier) {
    MaceraButton(
        text = "",
        onClick = onClick,
        modifier = modifier.size(56.dp),
        colors = listOf(Color(0xFFFF4500), Color(0xFFD84315)),
        borderColor = Color(0xFFBF360C),
        icon = Icons.Default.Close,
        isCircular = true
    )
}

@Composable
fun BtnGeneric(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    MaceraButton(
        text = text,
        onClick = onClick,
        modifier = modifier.height(64.dp),
        colors = listOf(Color(0xFF78909C), Color(0xFF546E7A)),
        borderColor = Color(0xFF37474F)
    )
}

@Composable
fun IconButtonCircular(iconRes: DrawableResource? = null, iconVector: ImageVector? = null, onClick: () -> Unit, modifier: Modifier = Modifier) {
    MaceraButton(
        text = "",
        onClick = onClick,
        modifier = modifier.size(72.dp),
        colors = listOf(Color(0xFF2F4F4F), Color(0xFF1A3333)),
        borderColor = Color(0xFF0F1F1F),
        icon = iconVector,
        iconRes = iconRes,
        isCircular = true
    )
}
