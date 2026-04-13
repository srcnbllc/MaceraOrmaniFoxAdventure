package com.zekaoformani.macera.ui.theme

import androidx.compose.ui.graphics.Color

// The Luminous Sanctuary - Crystal Cave Palette
val LuminousPrimary = Color(0xFFB5FFC2) // Büyülü Orman Yeşili
val LuminousPrimaryContainer = Color(0xFF3FFF8B) // Neon Yeşil Vurgu
val LuminousTertiary = Color(0xFFF8ACFF) // Büyülü Flora (Mor)
val LuminousSecondary = Color(0xFFFFB778) // Parlak Kehribar (Ember)
val LuminousSecondaryContainer = Color(0xFFFD9000) // Lava Turuncusu

val LuminousSurface = Color(0xFF031013) // Derin Uçurum (Karanlık Boşluk)
val LuminousOnSurface = Color(0xFFCDEBF2) // Yansımalı Açık Mavi-Beyaz
val LuminousSurfaceVariant = Color(0xFF0A262E) // Cam Arka Plan Katmanı
val LuminousOutlineVariant = Color(0xFF314C52) // Hayalet Çizgi (Ghost Border)

// Eski deklarelere köprü (Hata vermemesi için)
val Primary = LuminousPrimary
val SecondaryColor = LuminousSecondary
val BackgroundDark = LuminousSurface
val BackgroundLight = Color(0xFFEDFAEE)
val CardDark = LuminousSurfaceVariant
val CardLight = Color(0xFFFFFFFF)
val ScoreGold = Color(0xFFFFD700)
val HealthRed = Color(0xFFFF4500)
val PrimaryHover = LuminousPrimaryContainer
val SurfaceLight = Color(0xFFFFFFFF)

// Material 3 Dark Color Scheme eşleştirmeleri
val DarkPrimary = LuminousPrimary
val DarkOnPrimary = Color.Black
val DarkPrimaryContainer = LuminousPrimaryContainer
val DarkOnPrimaryContainer = Color.Black
val DarkBackground = LuminousSurface
val DarkOnBackground = LuminousOnSurface
val DarkSurface = LuminousSurface
val DarkOnSurface = LuminousOnSurface
val DarkSurfaceVariant = LuminousSurfaceVariant
val DarkOnSurfaceVariant = LuminousOnSurface
val DarkSecondary = LuminousSecondary
val DarkTertiary = LuminousTertiary

// Material 3 Light Color Scheme eşleştirmeleri (Genelde karanlık tema kullanılacak)
val LightPrimary = LuminousPrimary
val LightOnPrimary = Color.Black
val LightPrimaryContainer = LuminousPrimaryContainer
val LightOnPrimaryContainer = Color.Black
val LightBackground = BackgroundLight
val LightOnBackground = Color(0xFF231A10)
val LightSurface = SurfaceLight
val LightOnSurface = Color(0xFF231A10)
val LightSurfaceVariant = Color(0xFFE5E7EB) 
val LightOnSurfaceVariant = Color(0xFF231A10)
val LightSecondary = LuminousSecondary
val LightTertiary = LuminousTertiary
