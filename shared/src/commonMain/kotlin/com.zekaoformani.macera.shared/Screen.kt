package com.zekaoformani.macera

/**
 * Navigasyon rotalarını tanımlar.
 */
sealed class Screen(val route: String) {
    data object MainMenu : Screen("main_menu")
    data object Tutorial : Screen("tutorial")
    data object CharacterSelection : Screen("character_selection")
    data object ChapterSelection : Screen("chapter_selection")
    data object Leaderboard : Screen("leaderboard")
    data object BadgePool : Screen("badge_pool")
    data object GameScreen : Screen("game_screen")
}
