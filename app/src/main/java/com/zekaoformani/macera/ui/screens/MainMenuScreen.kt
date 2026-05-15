package com.zekaoformani.macera.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zekaoformani.macera.R
import com.zekaoformani.macera.data.DataManager
import com.zekaoformani.macera.data.SoundManager

@Composable
fun MainMenuScreen(
    selectedCharacterId: Int = 1,
    onNavigateToCharacterSelection: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToBadges: () -> Unit,
    onOpenSettings: () -> Unit,
    onNavigateToCamp: () -> Unit
) {
    val context = LocalContext.current
    val dataManager = remember { DataManager(context) }

    // --- SES VE YAŞAM DÖNGÜSÜ YÖNETİMİ EKLENDİ ---
    val soundManager = remember { SoundManager.getInstance(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var totalCoins by remember { mutableIntStateOf(0) }
    var highScore by remember { mutableIntStateOf(0) }

    // Ekran her yüklendiğinde kasa verilerini güncelle
    LaunchedEffect(Unit) {
        totalCoins = dataManager.getTotalCoins()
        highScore = dataManager.getHighScore()
    }

    // --- MÜZİK KONTROL BEKÇİSİ ---
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                // Ekrana dönüldüğünde veya kilit açıldığında müziği başlat
                Lifecycle.Event.ON_RESUME -> soundManager.playBackgroundMusic(R.raw.orman_muzigi)
                // Ekran kapandığında veya başka sayfaya geçildiğinde durdur
                Lifecycle.Event.ON_PAUSE -> soundManager.stopBackgroundMusic()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.menu_arkaplan)
                .build(),
            contentDescription = "Ana Menü Arkaplanı",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 800f
                    )
                )
        )

        // SAĞ ÜST KÖŞE ALTIN GÖSTERGESİ
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 24.dp)
                .background(Color.Black.copy(0.6f), CircleShape)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.item_coin),
                contentDescription = "Toplam Altın",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$totalCoins",
                color = Color(0xFFFFD700),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 4f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {

            // EN YÜKSEK SKOR TABELASI
            Surface(
                color = Color(0xFF4E342E).copy(alpha = 0.9f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700)),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "EN YÜKSEK SKOR: $highScore",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 8f))
                )
            }

            WoodenMenuButton(
                text = "MACERAYA BAŞLA",
                icon = Icons.Default.PlayArrow,
                onClick = onNavigateToCharacterSelection,
                isBig = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // KAMP BUTONU DÜZELTİLDİ (Artık diğerleriyle aynı tasarımda)
            WoodenMenuButton(
                text = "Gelişim Vadisi",
                icon = Icons.Default.LocalFireDepartment, // Kamp ateşi ikonu
                onClick = onNavigateToCamp,
                isBig = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                WoodenMenuButton(
                    text = "SKORLAR",
                    icon = Icons.Default.Leaderboard,
                    onClick = onNavigateToLeaderboard
                )
                WoodenMenuButton(
                    text = "ROZETLER",
                    icon = Icons.Default.EmojiEvents,
                    onClick = onNavigateToBadges
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            WoodenMenuButton(
                text = "AYARLAR",
                icon = Icons.Default.Settings,
                onClick = onOpenSettings
            )
        }
    }
}

@Composable
fun WoodenMenuButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isBig: Boolean = false
) {
    val woodLight = Color(0xFF8D6E63)
    val woodDark = Color(0xFF4E342E)
    val borderGold = Color(0xFFFFB300)

    Box(
        modifier = Modifier
            .width(if (isBig) 300.dp else 145.dp)
            .height(if (isBig) 70.dp else 55.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(woodLight, woodDark)))
            .border(3.dp, borderGold, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(if (isBig) 32.dp else 22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = if (isBig) 20.sp else 14.sp,
                fontWeight = FontWeight.Black,
                style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 8f))
            )
        }
    }
}