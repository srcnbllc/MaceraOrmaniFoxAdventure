package com.zekaoformani.macera.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.zekaoformani.macera.data.DataManager
import maceraormanifoxadventure.shared.generated.resources.Res
import maceraormanifoxadventure.shared.generated.resources.menu_arkaplan

// Rozet modelimiz
data class BadgeItem(
    val title: String,
    val description: String,
    val isUnlocked: Boolean,
    val icon: ImageVector,
    val tintColor: Color
)

@Composable
fun BadgePoolScreen(
    onNavigateBack: () -> Unit
) {
    val dataManager = remember { DataManager() }

    // Canlı verileri çekiyoruz
    val highScore = remember { dataManager.getHighScore() }
    val totalCoins = remember { dataManager.getTotalCoins() }
    val hasMonkey = remember { dataManager.isCharacterUnlocked(2) }
    val hasTiger = remember { dataManager.isCharacterUnlocked(3) }

    // Rozet başarımlarını otomatik hesaplıyoruz
    val dynamicBadges = listOf(
        BadgeItem("Acemi Koşucu", "500 Puan barajını geç.", highScore >= 500, Icons.Default.Star, Color(0xFFCD7F32)), // Bronz
        BadgeItem("Orman İzcis", "1.500 Puan barajını geç.", highScore >= 1500, Icons.Default.EmojiEvents, Color(0xFFE0E0E0)), // Gümüş
        BadgeItem("Efsanevi", "3.000 Puan barajını geç.", highScore >= 3000, Icons.Default.WorkspacePremium, Color(0xFFFFD700)), // Altın
        BadgeItem("Tasarruf", "Kasanda 200 Altın biriktir.", totalCoins >= 200, Icons.Default.Star, Color(0xFF4CAF50)), // Yeşil
        BadgeItem("Zengin", "Kasanda 1.000 Altın biriktir.", totalCoins >= 1000, Icons.Default.WorkspacePremium, Color(0xFF4CAF50)),
        BadgeItem("Koleksiyoner", "Tüm karakterleri aç.", (hasMonkey && hasTiger), Icons.Default.EmojiEvents, Color(0xFF9C27B0)) // Mor
    )

    Box(modifier = Modifier.fillMaxSize()) {

        // Arka plan
        AsyncImage(
            model = Res.drawable.menu_arkaplan,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(12.dp),
            colorFilter = ColorFilter.tint(
                Color(0xFF0F172A).copy(alpha = 0.85f),
                BlendMode.SrcAtop
            )
        )

        Scaffold(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // ÜST BAŞLIK KISMI
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "BAŞARIM ROZETLERİ",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ROZET IZGARASI (GRID)
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    columns = GridCells.Fixed(2), // 2 Sütunlu görünüm
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(dynamicBadges) { badge ->

                        // Kilitli ve Açık duruma göre renk ayarları
                        val bgColor = if (badge.isUnlocked) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.4f)
                        val borderColor = if (badge.isUnlocked) badge.tintColor.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f)
                        val iconColor = if (badge.isUnlocked) badge.tintColor else Color.Gray
                        val textColor = if (badge.isUnlocked) Color.White else Color.Gray

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = bgColor,
                            border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                            modifier = Modifier.height(140.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Rozet İkonu veya Kilit
                                Icon(
                                    imageVector = if (badge.isUnlocked) badge.icon else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(40.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Rozet Başlığı
                                Text(
                                    text = badge.title,
                                    color = textColor,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Görev Açıklaması
                                Text(
                                    text = badge.description,
                                    color = textColor.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
