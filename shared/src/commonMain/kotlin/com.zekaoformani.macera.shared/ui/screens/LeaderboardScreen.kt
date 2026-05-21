package com.zekaoformani.macera.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.zekaoformani.macera.data.DataManager
import maceraormanifoxadventure.shared.generated.resources.Res
import maceraormanifoxadventure.shared.generated.resources.menu_arkaplan

@Composable
fun LeaderboardScreen(
    playerScore: Int = 0,
    perLevelBest: Any? = null,
    onNavigateBack: () -> Unit = {}
) {
    val dataManager = remember { DataManager() }

    // Verileri çekiyoruz
    val lastScores = remember { dataManager.getLastScores() }
    val highScore = remember { dataManager.getHighScore() }

    Box(modifier = Modifier.fillMaxSize()) {
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
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

                        Text(
                            text = "GEÇMİŞ SKORLAR",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // EN YÜKSEK SKOR TABELASI
                    Surface(
                        color = Color(0xFFFFB300).copy(alpha = 0.20f),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFD700).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "👑 EN YÜKSEK SKOR",
                                color = Color(0xFFFFD700),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$highScore",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }

                // LİSTE ALANI (SON 10 OYUN)
                Box(modifier = Modifier.weight(1f)) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp, start = 16.dp, end = 16.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "SON 10 KOŞU",
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        if (lastScores.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Henüz hiç koşmadın.\nOrmana dalma vakti geldi!",
                                        color = Color.White.copy(alpha = 0.5f),
                                        fontSize = 16.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(lastScores) { index, score ->
                                val rankColor = when (index) {
                                    0 -> Color(0xFFFFD700)
                                    1 -> Color(0xFFE0E0E0)
                                    2 -> Color(0xFFCD7F32)
                                    else -> Color.White.copy(alpha = 0.5f)
                                }

                                Surface(
                                    color = Color.White.copy(alpha = 0.06f),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = rankColor.copy(alpha = 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, rankColor.copy(alpha = 0.3f)),
                                            modifier = Modifier.size(40.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "${index + 1}",
                                                    color = rankColor,
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        Text(
                                            text = "Koşu Skoru",
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontWeight = FontWeight.Medium,
                                            fontSize = 16.sp,
                                            modifier = Modifier.weight(1f)
                                        )

                                        Text(
                                            text = "$score",
                                            color = Color.White,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
