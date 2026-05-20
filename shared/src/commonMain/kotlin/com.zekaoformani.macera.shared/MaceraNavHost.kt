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
import com.zekaoformani.macera.data.DataManager

@Composable
fun MaceraNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.MainMenu.route
) {
    // KMP uyumlu instantiate
    val prefs = remember { GamePreferences() }
    val dataManager = remember { DataManager() }

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
        // 1. ANA MENÜ
        composable(Screen.MainMenu.route) {
            MainMenuScreen(
                onNavigateToCharacterSelection = {
                    if (!prefs.isTutorialShown()) navController.navigate(Screen.Tutorial.route)
                    else navController.navigate(Screen.CharacterSelection.route)
                },
                onNavigateToLeaderboard = { navController.navigate(Screen.Leaderboard.route) },
                onNavigateToBadges = { navController.navigate(Screen.BadgePool.route) },
                onOpenSettings = { showSettings = true },
                // YENİ EKLENEN SATIR BURASI:
                onNavigateToCamp = { navController.navigate("camp_screen") }
            )
        }

        // 2. TUTORIAL
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

        // 3. KARAKTER SEÇİMİ
        composable(Screen.CharacterSelection.route) {
            CharacterSelectionScreen(
                onBackClicked = { navController.popBackStack() },
                onStartAdventure = { characterId: Int ->
                    gameViewModel.selectCharacter(characterId)
                    // Doğrudan oyuna yönlendir
                    navController.navigate("game_screen/$characterId/1")
                }
            )
        }

        // 4. OYUN EKRANI
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
                    navController.popBackStack()
                },
                onLevelCompleted = { _, _, _ ->
                    // Sonsuz koşu için gerekirse buraya mantık eklenebilir
                }
            )
        }

        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(
                playerScore = totalScore,
                // T tipini burada açıkça belirtiyoruz:
                perLevelBest = emptyList<Triple<Int, Int, Int>>(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 6. ROZETLER
        composable(Screen.BadgePool.route) {
            BadgePoolScreen(
                badges = prefs.getBadges(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // KAMP EKRANI
        // KAMP EKRANI
        composable("camp_screen") {
            CampScreen(
                gamePrefs = prefs,
                dataManager = dataManager, // EKLENDİ: Ekran doğrudan buraya erişecek
                onSpendCoins = { amount ->
                    // Harcama işlemi direkt DataManager'ın orijinal sistemi üzerinden yapılacak
                    dataManager.spendCoins(amount)
                },
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