package com.zekaoformani.macera.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zekaoformani.macera.R

@Composable
fun TutorialScreen(
    characterId: Int = 1, // NavHost bağlantısı bozulmasın diye tutuyoruz
    onComplete: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        // --- ARKA PLAN ---
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(R.drawable.menu_arkaplan) // Ana menüdeki resmi kullanıyoruz
                .build(),
            contentDescription = "Eğitim Arkaplanı",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Resmin üzerini biraz daha fazla karartıyoruz ki yazılar okunsun
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.75f)))

        // --- İÇERİK PANELİ ---
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth(0.9f) // Ekranın %90'ını kaplar
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF4E342E), Color(0xFF2E1A14)))) // Ahşap geçişi
                    .border(4.dp, Color(0xFFFFB300), RoundedCornerShape(24.dp)) // Altın çerçeve
                    .padding(32.dp)
            ) {
                Text(
                    text = "NASIL OYNANIR?",
                    color = Color(0xFFFFD700),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 15f)),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // YUKARI KAYDIRMA (ZIPLAMA)
                TutorialInstructionRow(
                    icon = Icons.Default.KeyboardArrowUp,
                    text = "Zıplamak için parmağını hızlıca YUKARI kaydır!"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // AŞAĞI KAYDIRMA (EĞİLME)
                TutorialInstructionRow(
                    icon = Icons.Default.KeyboardArrowDown,
                    text = "Engellerin altından kaymak için AŞAĞI kaydır!"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ALTIN TOPLAMA
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.item_coin),
                        contentDescription = "Altın",
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Maceran boyunca ALTINLARI toplamayı unutma!",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // ANLADIM BUTONU
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.verticalGradient(listOf(Color(0xFF2ECC71), Color(0xFF27AE60))))
                        .border(3.dp, Color.White, RoundedCornerShape(16.dp))
                        .clickable { onComplete() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ANLADIM, BAŞLA!",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 8f))
                    )
                }
            }
        }
    }
}

// Tasarımı temiz tutmak için oluşturduğumuz küçük yardımcı fonksiyon
@Composable
fun TutorialInstructionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White.copy(0.15f), CircleShape)
                .border(2.dp, Color(0xFFFFD700), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}