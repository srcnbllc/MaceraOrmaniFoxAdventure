package com.zekaoformani.macera.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.zekaoformani.macera.R
import com.zekaoformani.macera.data.models.characters
import com.zekaoformani.macera.ui.components.BtnBack
import com.zekaoformani.macera.ui.components.BtnPlay
import com.zekaoformani.macera.ui.components.LeafParticleSystem
import kotlin.math.absoluteValue
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterSelectionScreen(
    onNavigateBack: () -> Unit,
    onCharacterSelected: (Int) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { characters.size })
    val coroutineScope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "charSelAnim")

    // Seçili karaktere hafif parlama efekti
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Orman Arka Planı
        Image(
            painter = painterResource(id = R.drawable.chapter1_theme),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.55f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.70f)
                        )
                    )
                )
        )

        // Particle System Elements
        LeafParticleSystem()

        var isVisible by remember { mutableStateOf(false) }
        val haptic = LocalHapticFeedback.current

        LaunchedEffect(Unit) {
            isVisible = true
        }

        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight / 4 },
                animationSpec = tween(1200, easing = EaseOutExpo)
            ) + fadeIn(animationSpec = tween(900)),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

            // Üst başlık
            Text(
                text = stringResource(R.string.title_choose_hero),
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.padding(top = 32.dp, start = 16.dp, end = 16.dp),
                style = TextStyle(
                    shadow = Shadow(Color.Black, blurRadius = 18f)
                )
            )

            // Karakter Carousel (tam alan - çerçevesiz)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 0.dp),
                pageSpacing = 0.dp,
                verticalAlignment = Alignment.CenterVertically
            ) { page ->
                val hero = characters[page]
                val pageOffset = (pagerState.currentPage - page) +
                        pagerState.currentPageOffsetFraction

                // Kutuyu Box içine alıyoruz ki görsel taşabilsin
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val scale = lerp(
                                start = 0.85f,
                                stop = 1.0f,
                                fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                            )
                            scaleX = scale
                            scaleY = scale
                            alpha = lerp(
                                start = 0.4f,
                                stop = 1f,
                                fraction = 1f - pageOffset.absoluteValue.coerceIn(0f, 1f)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val jumpOffset by animateFloatAsState(
                        targetValue = if (isPressed) -40f else 0f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "jumpAnim"
                    )
                    val floatingFoxOffsetY by infiniteTransition.animateFloat(
                        initialValue = -5f,
                        targetValue = 5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1800, easing = EaseInOutSine),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "floatingFox"
                    )

                    // Şeffaf Karakter Metinleri (Kutu/Kart Yok)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .padding(top = 220.dp, bottom = 24.dp, start = 16.dp, end = 16.dp), // Sadece metin konumlandırması için padding
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Karakter adı + arcade tarzı rozet (Flappy / Angry Birds hissi)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Image(
                                painter = painterResource(id = hero.arcadeBadgeRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 10.dp),
                                contentScale = ContentScale.Fit
                            )
                            Text(
                                text = stringResource(hero.nameRes),
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                maxLines = 2,
                                style = TextStyle(
                                    shadow = Shadow(Color.Black.copy(alpha = 0.5f), blurRadius = 8f)
                                )
                            )
                        }

                        // Karakter açıklaması
                        Text(
                            text = stringResource(hero.descRes),
                            color = Color(0xFFFFD54F),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            maxLines = 2,
                            modifier = Modifier.padding(top = 8.dp),
                            style = TextStyle(
                                shadow = Shadow(Color.Black.copy(alpha = 0.5f), blurRadius = 4f)
                            )
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Özellik Kartı (Daha dengeli font ve ikonlar)
                        Surface(
                            color = Color.Black.copy(alpha = 0.35f), // Daha temiz bir siyah okunaklılığı artırır
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Hız
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        stringResource(R.string.lbl_speed),
                                        color = Color(0xFFFFCC80),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        for (i in 1..5) {
                                            Icon(
                                                imageVector = if (i <= hero.speedStars)
                                                    Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = null,
                                                tint = if (i <= hero.speedStars)
                                                    Color(0xFFFFD700) else Color.White.copy(alpha = 0.4f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                // Zıplama
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        stringResource(R.string.lbl_jump),
                                        color = Color(0xFFFFCC80),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        for (i in 1..5) {
                                            Icon(
                                                imageVector = if (i <= hero.jumpStars)
                                                    Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = null,
                                                tint = if (i <= hero.jumpStars)
                                                    Color(0xFF80DEEA) else Color.White.copy(alpha = 0.4f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }

                                // Dikey ayraç 2
                                Box(
                                    modifier = Modifier
                                        .width(1.5.dp)
                                        .height(40.dp)
                                        .background(Color.White.copy(alpha = 0.25f))
                                )

                                // Dayanıklılık (Durability)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        stringResource(R.string.lbl_durability),
                                        color = Color(0xFFFFCC80),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        for (i in 1..5) {
                                            Icon(
                                                imageVector = if (i <= hero.durabilityStars)
                                                    Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = null,
                                                tint = if (i <= hero.durabilityStars)
                                                    Color(0xFFFF7043) else Color.White.copy(alpha = 0.4f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Karakter görseli — şeffaf sprite, kutudan tamamen bağımsız
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 10.dp)
                            .size(230.dp)
                            .offset(y = jumpOffset.dp + floatingFoxOffsetY.dp)
                    ) {
                        // Sihirli Kaide (Magic Pedestal)
                        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().align(Alignment.BottomCenter)) {
                            val ovalWidth = size.width * 0.65f
                            val ovalHeight = size.height * 0.15f
                            val left = (size.width - ovalWidth) / 2
                            val top = size.height - ovalHeight - 10f

                            drawOval(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFF69F0AE).copy(alpha = 0.9f), Color(0xFF69F0AE).copy(alpha = 0f)),
                                    center = androidx.compose.ui.geometry.Offset(size.width / 2, top + ovalHeight / 2),
                                    radius = ovalWidth / 1.5f
                                ),
                                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                                size = androidx.compose.ui.geometry.Size(ovalWidth, ovalHeight)
                            )
                        }

                        Image(
                            painter = painterResource(id = hero.imageRes),
                            contentDescription = stringResource(id = hero.nameRes),
                            contentScale = ContentScale.Fit, // ⭐ Crop değil Fit — karakter kare görünmez
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
                                )
                        )
                    }
                }
            }

            // Floating animasyonu için
            val floatingOffsetY by infiniteTransition.animateFloat(
                initialValue = -4f,
                targetValue = 4f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500, easing = EaseInOutSine),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "arrowFloat"
            )

            // Pager Göstergeleri (dot indicators) + Sol/Sağ Oklar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), // daha rahat sığması için padding'i azalttık
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Sol ok
                val leftInteractionSource = remember { MutableInteractionSource() }
                val isLeftPressed by leftInteractionSource.collectIsPressedAsState()
                val leftScale by animateFloatAsState(targetValue = if (isLeftPressed) 0.85f else 1f, animationSpec = spring(stiffness = Spring.StiffnessLow), label="l_scale")

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        coroutineScope.launch {
                            if (pagerState.currentPage > 0)
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = floatingOffsetY.dp)
                        .graphicsLayer { scaleX = leftScale; scaleY = leftScale }
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage > 0)
                                Color(0xFFFF8C00).copy(alpha = 0.85f)
                            else Color.White.copy(alpha = 0.15f)
                        ),
                    interactionSource = leftInteractionSource
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.cd_prev_char),
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Dot göstergeleri
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until characters.size) {
                        val isSelected = pagerState.currentPage == i
                        Box(
                            modifier = Modifier
                                .size(
                                    width = if (isSelected) 36.dp else 12.dp,
                                    height = 12.dp
                                )
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color(0xFFFFC107) // Amber
                                    else Color.White.copy(alpha = 0.4f)
                                )
                        )
                    }
                }

                // Sağ ok
                val rightInteractionSource = remember { MutableInteractionSource() }
                val isRightPressed by rightInteractionSource.collectIsPressedAsState()
                val rightScale by animateFloatAsState(targetValue = if (isRightPressed) 0.85f else 1f, animationSpec = spring(stiffness = Spring.StiffnessLow), label="r_scale")

                IconButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        coroutineScope.launch {
                            if (pagerState.currentPage < characters.size - 1)
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .offset(y = floatingOffsetY.dp)
                        .graphicsLayer { scaleX = rightScale; scaleY = rightScale }
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage < characters.size - 1)
                                Color(0xFFFF8C00).copy(alpha = 0.85f)
                            else Color.White.copy(alpha = 0.15f)
                        ),
                    interactionSource = rightInteractionSource
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(R.string.cd_next_char),
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // MACERAYA BAŞLA Butonu
            val currentHero = characters[pagerState.currentPage]
            BtnPlay(
                text = stringResource(R.string.btn_start_adventure),
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onCharacterSelected(currentHero.id) 
                },
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth()
                    .height(72.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))
        }

        } // end of AnimatedVisibility
        
        // Geri Butonu (Sol Üst)
        Box(
            modifier = Modifier
                .safeDrawingPadding()
                .padding(12.dp)
        ) {
            BtnBack(onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onNavigateBack()
            })
        }
    }
}
