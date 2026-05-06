package com.zekaoformani.macera.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zekaoformani.macera.R
import com.zekaoformani.macera.data.models.characters
import com.zekaoformani.macera.data.models.levels

@Composable
fun ChapterSelectionScreen(
    characterId: Int,
    unlockedChapter: Int,
    levelStars: (Int) -> Int,
    levelCompleted: (Int) -> Boolean,
    onNavigateBack: () -> Unit,
    onChapterSelected: (Int) -> Unit
) {
    val context = LocalContext.current
    val heroData = characters.find { it.id == characterId } ?: characters[0]
    val listState = rememberLazyListState()

    val infiniteTransition = rememberInfiniteTransition(label = "mapChar")
    val charFloatY by infiniteTransition.animateFloat(
        initialValue = -10f, targetValue = 10f,
        animationSpec = infiniteRepeatable(tween(1200, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "float"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Arka Plan (Ana menüdeki bütünlüğü korumak için aynı resmi kullanıyoruz)
        AsyncImage(
            model = ImageRequest.Builder(context).data(R.drawable.menu_arkaplan).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))

        Column(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {

            // Üst Bar
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(Color.Black.copy(0.5f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = Color.White)
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    "BÖLÜM SEÇİMİ",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 10f))
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp, top = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                reverseLayout = true
            ) {
                items(levels) { level ->
                    val isUnlocked = level.id <= unlockedChapter
                    val stars = levelStars(level.id)

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {

                        MapLevelNode(
                            // BURASI DÜZELDİ: Artık "Bölüm 1" yerine gerçek ismini çekiyor
                            levelName = stringResource(level.nameRes),
                            levelNumber = level.id,
                            isUnlocked = isUnlocked,
                            stars = stars,
                            onClick = { if (isUnlocked) onChapterSelected(level.id) }
                        )

                        if (level.id == unlockedChapter) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(heroData.imageRes).build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(90.dp)
                                    .offset(y = (-85 + charFloatY).dp, x = 65.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    if (level.id < levels.size) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(60.dp)
                                .background(Brush.verticalGradient(listOf(Color(0xFFFFD700).copy(0.5f), Color.Transparent)))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MapLevelNode(
    levelName: String,
    levelNumber: Int,
    isUnlocked: Boolean,
    stars: Int,
    onClick: () -> Unit
) {
    val woodDark = Color(0xFF4E342E)
    val woodLight = Color(0xFF8D6E63)
    val gold = Color(0xFFFFD700)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 12.dp)
            .width(240.dp) // Uzun isimler için genişliği biraz artırdık
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isUnlocked) Brush.verticalGradient(listOf(woodLight, woodDark))
                else Brush.verticalGradient(listOf(Color.Gray.copy(0.5f), Color.DarkGray.copy(0.5f)))
            )
            .border(3.dp, if (isUnlocked) gold else Color.DarkGray, RoundedCornerShape(20.dp))
            .clickable { if (isUnlocked) onClick() }
            .padding(16.dp)
    ) {
        if (!isUnlocked) {
            Icon(Icons.Default.Lock, null, tint = Color.White.copy(0.5f), modifier = Modifier.size(24.dp))
        } else {
            // "Bölüm X" yazısını küçük, gerçek ismi büyük yaptık
            Text(
                "BÖLÜM $levelNumber",
                color = gold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                levelName,
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 4f))
            )

            Row(modifier = Modifier.padding(top = 8.dp)) {
                repeat(3) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < stars) gold else Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}