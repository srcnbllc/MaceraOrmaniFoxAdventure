package com.zekaoformani.macera.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha // Hata aldığın kritik import burası
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.zekaoformani.macera.R
import com.zekaoformani.macera.data.DataManager
import com.zekaoformani.macera.data.GamePreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampScreen(
    gamePrefs: GamePreferences,
    dataManager: DataManager,
    onSpendCoins: (Int) -> Boolean,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    // Takip Edilen Seviye ve Altın Bilgileri
    var totalCoins by remember { mutableIntStateOf(dataManager.getTotalCoins()) }
    var tentLevel by remember { mutableIntStateOf(gamePrefs.getTentLevel()) }
    var campfireLevel by remember { mutableIntStateOf(gamePrefs.getCampfireLevel()) }
    var dummyLevel by remember { mutableIntStateOf(gamePrefs.getDummyLevel()) }

    // Maksimum Seviye Sınırları
    val maxTentLevel = 2
    val maxCampfireLevel = 8
    val maxDummyLevel = 5

    // Dinamik Fiyatlandırma
    val tentUpgradeCost = if (tentLevel == 0) 500 else 1000
    val campfireUpgradeCost = 750 + (campfireLevel * 100)
    val dummyUpgradeCost = (dummyLevel + 1) * 300

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. KATMAN: Ana menüyle uyumlu orman manzarası
        AsyncImage(
            model = ImageRequest.Builder(context).data(R.drawable.menu_arkaplan).build(),
            contentDescription = "Oba Arkaplanı",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. KATMAN: Arayüzün okunabilirliği için gradyan karartma
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    listOf(Color.Black.copy(0.3f), Color.Black.copy(0.7f))
                ))
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text("GELİŞİM VADİSİ",
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 8f))
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Geri", modifier = Modifier.size(32.dp))
                        }
                    },
                    actions = {
                        // Üst paneldeki altın göstergesi
                        Row(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .background(Color.Black.copy(0.6f), RoundedCornerShape(50))
                                .border(2.dp, Color(0xFFFFB300), RoundedCornerShape(50))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.item_coin),
                                contentDescription = "Altın",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$totalCoins",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color(0xFFFFD700)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                // --- 1. DİNLENME ÇADIRI ---
                GameUpgradePanel(
                    title = "Dinlenme Çadırı",
                    description = "Kahramanlarının savunma hattını kur! Seviye 1'de kalkanı olmayan karakterlere hayati bir kalkan hakkı sağlar. Seviye 2'de ise tüm kahramanlara ekstra kalkan ekleyerek onları ormanın tehlikelerine karşı korur.",
                    icon = Icons.Default.Home,
                    currentLevel = tentLevel,
                    maxLevel = maxTentLevel,
                    cost = tentUpgradeCost,
                    isLocked = false,
                    onUpgradeClick = {
                        if (onSpendCoins(tentUpgradeCost)) {
                            gamePrefs.setTentLevel(tentLevel + 1)
                            tentLevel = gamePrefs.getTentLevel()
                            totalCoins = dataManager.getTotalCoins()
                            Toast.makeText(context, "Çadır Geliştirildi!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Yetersiz Altın!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // --- 2. KAMP ATEŞİ ---
                val canUnlockCampfire = tentLevel > 0
                GameUpgradePanel(
                    title = "Kamp Ateşi",
                    description = if (!canUnlockCampfire) "Bu mistik ateşi yakmak için önce Dinlenme Çadırı'nı kurmalısın."
                    else "Ateşin gücüyle savunmanı besle! Her geliştirmede kalkanının dayanma süresini 5 saniye daha uzatır. Maksimum seviyeye ulaştığında 60 saniyelik devasa bir koruma kalkanın olur!",
                    icon = Icons.Default.LocalFireDepartment,
                    currentLevel = campfireLevel,
                    maxLevel = maxCampfireLevel,
                    cost = campfireUpgradeCost,
                    isLocked = !canUnlockCampfire,
                    onUpgradeClick = {
                        if (onSpendCoins(campfireUpgradeCost)) {
                            gamePrefs.setCampfireLevel(campfireLevel + 1)
                            campfireLevel = gamePrefs.getCampfireLevel()
                            totalCoins = dataManager.getTotalCoins()
                            Toast.makeText(context, "Ateş Güçlendirildi!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Yetersiz Altın!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                // --- 3. EĞİTİM KUKLASI ---
                GameUpgradePanel(
                    title = "Eğitim Kuklası",
                    description = if (campfireLevel == 0) "Kukla antrenmanlarına başlamak için önce Kamp Ateşi'ni yakmalısın."
                    else "Antrenman yap ve altınları mıknatıs gibi çek! Karakterinin etrafında görünmez bir menzil oluşturur. Artık altınların tam üzerinden geçmene gerek yok, onlar sana koşacaktır!",
                    icon = Icons.Default.AccessibilityNew,
                    currentLevel = dummyLevel,
                    maxLevel = maxDummyLevel,
                    cost = dummyUpgradeCost,
                    isLocked = campfireLevel == 0,
                    onUpgradeClick = {
                        if (onSpendCoins(dummyUpgradeCost)) {
                            gamePrefs.setDummyLevel(dummyLevel + 1)
                            dummyLevel = gamePrefs.getDummyLevel()
                            totalCoins = dataManager.getTotalCoins()
                            Toast.makeText(context, "Kukla Geliştirildi!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Yetersiz Altın!", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun GameUpgradePanel(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    currentLevel: Int,
    maxLevel: Int,
    cost: Int,
    isLocked: Boolean,
    onUpgradeClick: () -> Unit
) {
    val woodLight = if (isLocked) Color(0xFF616161) else Color(0xFF5D4037)
    val woodDark = if (isLocked) Color(0xFF212121) else Color(0xFF3E2723)
    val borderColor = if (isLocked) Color(0xFF757575) else Color(0xFFFFB300)
    val iconTint = when {
        isLocked -> Color.DarkGray
        currentLevel > 0 -> Color(0xFFFFD700)
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(woodLight, woodDark)))
            .border(3.dp, borderColor, RoundedCornerShape(16.dp))
            .alpha(if (isLocked) 0.8f else 1f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(0.4f))
                        .border(2.dp, borderColor.copy(0.5f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isLocked) Color.Gray else Color.White,
                        style = TextStyle(shadow = Shadow(Color.Black, blurRadius = 4f))
                    )
                    Text(
                        text = description,
                        fontSize = 13.sp,
                        color = if (isLocked) Color.DarkGray else Color(0xFFD7CCC8),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seviye İlerleme Çubukları
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (i in 0 until maxLevel) {
                    val boxColor = when {
                        isLocked -> Color.DarkGray
                        i < currentLevel -> Color(0xFF69F0AE)
                        else -> Color.Black.copy(0.5f)
                    }
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .weight(1f)
                            .clip(RoundedCornerShape(2.dp))
                            .background(boxColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (currentLevel >= maxLevel && !isLocked) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(0.3f))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("MAKSİMUM SEVİYE", color = Color(0xFF69F0AE), fontWeight = FontWeight.Black, fontSize = 16.sp)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isLocked) Brush.verticalGradient(listOf(Color(0xFF757575), Color(0xFF424242)))
                            else Brush.verticalGradient(listOf(Color(0xFF4CAF50), Color(0xFF2E7D32)))
                        )
                        .border(2.dp, if (isLocked) Color.Gray else Color(0xFF81C784), RoundedCornerShape(12.dp))
                        .clickable(enabled = !isLocked) { onUpgradeClick() },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isLocked) {
                        Icon(Icons.Default.Lock, contentDescription = "Kilitli", tint = Color.LightGray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("KİLİTLİ", color = Color.LightGray, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    } else {
                        Image(painter = painterResource(id = R.drawable.item_coin), contentDescription = null, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "$cost", color = Color(0xFFFFD700), fontWeight = FontWeight.Black, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("GELİŞTİR", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}