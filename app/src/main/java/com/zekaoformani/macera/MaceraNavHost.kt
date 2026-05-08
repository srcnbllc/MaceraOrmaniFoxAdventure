package com.zekaoformani.macera

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zekaoformani.macera.data.GamePreferences
import com.zekaoformani.macera.ui.screens.*
import com.zekaoformani.macera.ui.components.SettingsOverlay
import com.zekaoformani.macera.ui.viewmodel.GameViewModel
import com.zekaoformani.macera.ui.viewmodel.GameViewModelFactory

@Composable
fun MaceraNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.MainMenu.route
) {
    val context = LocalContext.current
    val prefs = remember { GamePreferences(context) }
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(prefs))

    // Ayarlar State'leri
    var showSettings by remember { mutableStateOf(false) }
    var settingsMusicOn by remember { mutableStateOf(prefs.isMusicEnabled()) }
    var settingsSfxOn by remember { mutableStateOf(prefs.isSfxEnabled()) }

    // Canlı Skor
    val totalScore by gameViewModel.totalScore.collectAsState()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn(tween(350)) },
        exitTransition = { fadeOut(tween(350)) },
        popEnterTransition = { fadeIn(tween(350)) },
        popExitTransition = { fadeOut(tween(350)) }
    ) {

        // 1. ANA MENÜ
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onNavigateToCharacterSelection = {
                    if (!prefs.isTutorialShown()) navController.navigate(Screen.Tutorial.route)
                    else navController.navigate(Screen.CharacterSelection.route)
                },
                onNavigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) },
                onNavigateToBadges = { navController.navigate(Screen.BadgePool.route) },
                onOpenSettings = { showSettings = true }
            )
        }

        // 2. TUTORIAL (EĞİTİM EKRANI)
        composable(Screen.Tutorial.route) {
            TutorialScreen(
                characterId = 1,
                onComplete = {
                    prefs.setTutorialShown(true)
                    navController.navigate(Screen.CharacterSelection.route) {
                        popUpTo(Screen.Tutorial.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. KARAKTER SEÇİMİ (Mağaza)
        composable(Screen.CharacterSelection.route) {
            CharacterSelectionScreen(
                onBackClicked = { navController.popBackStack() },
                onStartAdventure = { characterId: Int ->
                    gameViewModel.selectCharacter(characterId)

                    // GÜNCELLEME: Bölüm seçimi atlandı, doğrudan oyuna (1. Hız seviyesi ile) giriliyor!
                    navController.navigate("game_screen/$characterId/1")
                }
            )
        }

        // DİKKAT: 4. BÖLÜM SEÇİM EKRANI (ChapterSelectionScreen) BURADAN TAMAMEN KALDIRILDI

        // 5. OYUN EKRANI
        composable(
            route = "game_screen/{characterId}/{chapterId}",
            arguments = listOf(
                navArgument("characterId") { type = NavType.IntType },
                navArgument("chapterId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val charId = backStackEntry.arguments?.getInt("characterId") ?: 1
            val chapId = backStackEntry.arguments?.getInt("chapterId") ?: 1

            GameScreen(
                characterId = charId,
                chapterId = chapId,
                onNavigateBack = {
                    // Oyundan çıkınca Ana Menüye dönsün istersen popUpTo kullanabiliriz
                    // Şimdilik standart geri gitme (Karakter Seçimine döner)
                    navController.popBackStack()
                },
                onLevelCompleted = { _, _, _ ->
                    // Sonsuz koşuda level bitme durumu olmadığı için burası boş bırakıldı
                }
            )
        }

        // 6. SKORLAR TABLOSU
        composable(Screen.Leaderboard.route) {
            val perLevel = com.zekaoformani.macera.data.models.levels.map { lvl ->
                Triple(lvl.id, prefs.getLevelBestScore(lvl.id), prefs.getLevelStars(lvl.id))
            }
            LeaderboardScreen(
                playerScore = totalScore,
                perLevelBest = perLevel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 7. ROZETLER
        composable(Screen.BadgePool.route) {
            BadgePoolScreen(
                badges = prefs.getBadges(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }

    // AYARLAR KATMANI
    if (showSettings) {
        SettingsOverlay(
            musicEnabled = settingsMusicOn,
            sfxEnabled = settingsSfxOn,
            onMusicToggle = { settingsMusicOn = it; prefs.setMusicEnabled(it) },
            onSfxToggle = { settingsSfxOn = it; prefs.setSfxEnabled(it) },
            onDismiss = { showSettings = false }
        )
    }
}