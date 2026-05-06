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

    // Canlı Skor (Skor tablosu/Leaderboard için hala lazım)
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

        // 1. ANA MENÜ (Güncellendi: totalScore parametresi kaldırıldı)
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
                    navController.navigate("chapter_selection/$characterId")
                }
            )
        }

        // 4. BÖLÜM SEÇİM HARİTASI
        composable(
            route = "chapter_selection/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val charId = backStackEntry.arguments?.getInt("characterId") ?: 1
            ChapterSelectionScreen(
                characterId = charId,
                unlockedChapter = prefs.getUnlockedChapter(),
                levelStars = { levelId -> prefs.getLevelStars(levelId) },
                levelCompleted = { levelId -> prefs.isLevelCompleted(levelId) },
                onNavigateBack = { navController.popBackStack() },
                onChapterSelected = { chapterId ->
                    navController.navigate("game_screen/$charId/$chapterId")
                }
            )
        }

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
                onNavigateBack = { navController.popBackStack() },
                onLevelCompleted = { levelId, scoreEarned, starsEarned ->
                    gameViewModel.addScore(scoreEarned)
                    prefs.setLevelResult(levelId, starsEarned, scoreEarned)
                    prefs.addBadge("badge_level_$levelId")
                    if (starsEarned == 3) prefs.addBadge("badge_perfect_$levelId")
                    gameViewModel.unlockChapter(levelId + 1)

                    if (levelId < 10) {
                        val nextLevel = levelId + 1
                        navController.navigate("game_screen/$charId/$nextLevel") {
                            popUpTo("game_screen/$charId/$levelId") { inclusive = true }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        // 6. SKORLAR
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