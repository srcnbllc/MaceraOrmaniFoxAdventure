package com.zekaoformani.macera.ui.screens

import android.os.Build.VERSION.SDK_INT
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.zekaoformani.macera.R
import com.zekaoformani.macera.data.DataManager

data class HeroSelectionData(
    val id: Int,
    val name: String,
    val description: String,
    val speedStars: Int,
    val jumpStars: Int,
    val durabilityStars: Int,
    val imageRes: Int
)

@Composable
fun CharacterSelectionScreen(
    onBackClicked: () -> Unit = {},
    onStartAdventure: (characterId: Int) -> Unit = {}
) {
    val context = LocalContext.current
    val dataManager = remember { DataManager(context) }
    var currentCoins by remember { mutableIntStateOf(dataManager.getTotalCoins()) }

    val characterPrices = mapOf(
        1 to 0,
        2 to 750,
        3 to 1500
    )

    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    val heroList = listOf(
        HeroSelectionData(1, "SWIFT FOX", "Dengeli ve kurnaz. Ormanın her köşesini bilir!", 4, 3, 2, R.drawable.fox),
        HeroSelectionData(2, "CRAZY MONKEY", "Çevik ve çılgın! Herkesten daha yükseğe zıplar.", 5, 5, 1, R.drawable.monkey),
        HeroSelectionData(3, "WILD TIGER", "Korkusuz ve güçlü! Karşısına çıkan engelleri rahatça parçalar.", 4, 2, 5, R.drawable.tigger)
    )

    var currentIndex by remember { mutableIntStateOf(0) }
    val currentHero = heroList[currentIndex]

    val infiniteTransition = rememberInfiniteTransition(label = "charFloat")
    val charFloatY by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "float"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.karakter_menu)
                .build(),
            contentDescription = "Karakter Seçim Arka Planı",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ÜST BAR: DÜZELTİLEN KISIM
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Sola Dayalı Geri Butonu
                IconButton(
                    onClick = onBackClicked,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .background(Color.Black.copy(0.5f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = Color.White)
                }

                // Tam Ortalanmış Başlık
                Text(
                    text = "KAHRAMANINI SEÇ",
                    color = Color(0xFFFFD700),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 10f)),
                    modifier = Modifier.align(Alignment.Center)
                )

                // Sağa Dayalı Altın Göstergesi (Bozulma engellendi)
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(Color.Black.copy(0.6f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.item_coin),
                        contentDescription = "Altın",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$currentCoins",
                        color = Color(0xFFFFD700),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        if (currentIndex > 0) currentIndex--
                        else currentIndex = heroList.size - 1
                    },
                    modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape).size(56.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }

                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier.height(280.dp).width(200.dp)
                ) {
                    Canvas(modifier = Modifier.size(180.dp, 50.dp).offset(y = 15.dp)) {
                        drawOval(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFF69F0AE).copy(alpha = 0.6f), Color.Transparent),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.width / 1.5f
                            )
                        )
                    }

                    val isUnlocked = dataManager.isCharacterUnlocked(currentHero.id)

                    AnimatedContent(
                        targetState = currentHero,
                        transitionSpec = {
                            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                        }, label = "char_anim"
                    ) { char ->
                        Box(contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = char.imageRes,
                                imageLoader = imageLoader,
                                contentDescription = char.name,
                                modifier = Modifier
                                    .size(240.dp)
                                    .offset(y = (20 + charFloatY).dp),
                                contentScale = ContentScale.Fit,
                                // DÜZELTİLEN KISIM: Gri kareyi önlemek için Modulate kullanıldı
                                colorFilter = if (!isUnlocked) ColorFilter.tint(Color.Gray, androidx.compose.ui.graphics.BlendMode.Modulate) else null
                            )

                            if (!isUnlocked) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Kilitli",
                                    tint = Color.White.copy(0.8f),
                                    modifier = Modifier.size(80.dp).offset(y = (20 + charFloatY).dp)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = {
                        if (currentIndex < heroList.size - 1) currentIndex++
                        else currentIndex = 0
                    },
                    modifier = Modifier.background(Color.Black.copy(0.4f), CircleShape).size(56.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Black.copy(0.6f))
                    .border(2.dp, Color(0xFFFFB300).copy(0.5f), RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = currentHero.name,
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(letterSpacing = 2.sp, shadow = Shadow(Color(0xFFFF9800), blurRadius = 15f))
                )

                Text(
                    text = currentHero.description,
                    color = Color(0xFFFFD54F),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                HeroStatRow("SPEED", currentHero.speedStars, Color(0xFFFFC107))
                Spacer(modifier = Modifier.height(8.dp))
                HeroStatRow("JUMP", currentHero.jumpStars, Color(0xFF29B6F6))
                Spacer(modifier = Modifier.height(8.dp))
                HeroStatRow("DURABILITY", currentHero.durabilityStars, Color(0xFFEF5350))

                Spacer(modifier = Modifier.height(24.dp))

                val isUnlocked = dataManager.isCharacterUnlocked(currentHero.id)
                val price = characterPrices[currentHero.id] ?: 9999

                if (isUnlocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(65.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.verticalGradient(listOf(Color(0xFF8D6E63), Color(0xFF4E342E))))
                            .border(3.dp, Color(0xFFFFB300), RoundedCornerShape(16.dp))
                            .clickable { onStartAdventure(currentHero.id) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MACERAYA BAŞLA",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 5f))
                        )
                    }
                } else {
                    val canAfford = currentCoins >= price
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(65.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (canAfford) Brush.verticalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF8F00)))
                                else Brush.verticalGradient(listOf(Color.Gray, Color.DarkGray))
                            )
                            .border(3.dp, if (canAfford) Color.White else Color.LightGray, RoundedCornerShape(16.dp))
                            .clickable(enabled = canAfford) {
                                if (dataManager.spendCoins(price)) {
                                    dataManager.unlockCharacter(currentHero.id)
                                    currentCoins = dataManager.getTotalCoins()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = painterResource(id = R.drawable.item_coin), contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (canAfford) "KİLİDİ AÇ ($price)" else "YETERSİZ ALTIN ($price)",
                                color = if (canAfford) Color.Black else Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeroStatRow(title: String, activeStars: Int, starColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            modifier = Modifier.width(110.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 1..5) {
                Icon(
                    imageVector = if (i <= activeStars) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = null,
                    tint = if (i <= activeStars) starColor else Color.Gray.copy(alpha = 0.4f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}