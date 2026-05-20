package com.zekaoformani.macera.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MaceraPrefs", Context.MODE_PRIVATE)

    private val _selectedCharacterFlow = MutableStateFlow(prefs.getInt("selected_character", 1))
    val selectedCharacterFlow: StateFlow<Int> = _selectedCharacterFlow.asStateFlow()

    private val _unlockedChaptersFlow = MutableStateFlow(getUnlockedChaptersList())
    val unlockedChaptersFlow: StateFlow<List<Int>> = _unlockedChaptersFlow.asStateFlow()

    private val _totalScoreFlow = MutableStateFlow(prefs.getInt("total_score", 0))
    val totalScoreFlow: StateFlow<Int> = _totalScoreFlow.asStateFlow()

    // Tutorial gösterildi mi?
    fun isTutorialShown(): Boolean = prefs.getBoolean("tutorial_shown", false)
    fun setTutorialShown(shown: Boolean) {
        prefs.edit().putBoolean("tutorial_shown", shown).apply()
    }

    // Müzik & SFX ayarları
    fun isMusicEnabled(): Boolean = prefs.getBoolean("music_enabled", true)
    fun setMusicEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("music_enabled", enabled).apply()
    }

    fun isSfxEnabled(): Boolean = prefs.getBoolean("sfx_enabled", true)
    fun setSfxEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("sfx_enabled", enabled).apply()
    }

    fun getUnlockedChapter(): Int {
        return prefs.getInt("unlocked_chapter", 1)
    }

    fun isLevelCompleted(levelId: Int): Boolean =
        prefs.getBoolean("level_${levelId}_completed", false)

    fun getLevelStars(levelId: Int): Int =
        prefs.getInt("level_${levelId}_stars", 0)

    fun getLevelBestScore(levelId: Int): Int =
        prefs.getInt("level_${levelId}_best_score", 0)

    fun setLevelResult(levelId: Int, starsEarned: Int, score: Int) {
        val safeStars = starsEarned.coerceIn(0, 3)
        val currentStars = getLevelStars(levelId)
        val currentBest = getLevelBestScore(levelId)
        prefs.edit()
            .putBoolean("level_${levelId}_completed", true)
            .putInt("level_${levelId}_stars", maxOf(currentStars, safeStars))
            .putInt("level_${levelId}_best_score", maxOf(currentBest, score))
            .apply()
    }

    fun getBadges(): Set<String> =
        prefs.getStringSet("badges", emptySet()) ?: emptySet()

    fun addBadge(badgeId: String) {
        val updated = (getBadges() + badgeId).toSet()
        prefs.edit().putStringSet("badges", updated).apply()
    }

    private fun getUnlockedChaptersList(): List<Int> {
        val maxChapter = getUnlockedChapter()
        return (1..maxChapter).toList()
    }

    fun setSelectedCharacter(id: Int) {
        prefs.edit().putInt("selected_character", id).apply()
        _selectedCharacterFlow.value = id
    }

    fun unlockChapter(chapterId: Int) {
        val currentMax = getUnlockedChapter()
        val clamped = chapterId.coerceAtMost(10)
        if (clamped > currentMax) {
            prefs.edit().putInt("unlocked_chapter", clamped).apply()
            _unlockedChaptersFlow.value = (1..clamped).toList()
        }
    }

    fun updateScore(points: Int) {
        val current = prefs.getInt("total_score", 0)
        val newScore = current + points
        prefs.edit().putInt("total_score", newScore).apply()
        _totalScoreFlow.value = newScore
    }

    // Legacy support for addScore if needed
    fun addScore(points: Int) = updateScore(points)

    // =================================================================
    // KAMP VE GELİŞTİRME SİSTEMİ (Yeni Eklentiler)
    // =================================================================

    private val KEY_TENT_LEVEL = "tent_level"
    private val KEY_CAMPFIRE_LEVEL = "campfire_level" // YENİ: Artık seviye tutuyor
    private val KEY_DUMMY_LEVEL = "dummy_level"

    fun getTentLevel(): Int = prefs.getInt(KEY_TENT_LEVEL, 0)

    fun setTentLevel(level: Int) {
        val safeLevel = level.coerceIn(0, 2)
        prefs.edit().putInt(KEY_TENT_LEVEL, safeLevel).apply()
    }

    // --- KAMP ATEŞİ SEVİYE SİSTEMİ (Değişen Kısım) ---
    fun getCampfireLevel(): Int = prefs.getInt(KEY_CAMPFIRE_LEVEL, 0)

    fun setCampfireLevel(level: Int) {
        prefs.edit().putInt(KEY_CAMPFIRE_LEVEL, level).apply()
    }

    fun getCharacterShieldUpgradeLevel(characterId: Int): Int =
        prefs.getInt("shield_upgrade_char_$characterId", 0)

    fun upgradeCharacterShield(characterId: Int): Boolean {
        val currentLevel = getCharacterShieldUpgradeLevel(characterId)
        if (currentLevel < 8) {
            prefs.edit().putInt("shield_upgrade_char_$characterId", currentLevel + 1).apply()
            return true
        }
        return false
    }

    fun getDummyLevel(): Int = prefs.getInt(KEY_DUMMY_LEVEL, 0)

    fun setDummyLevel(level: Int) {
        prefs.edit().putInt(KEY_DUMMY_LEVEL, level).apply()
    }
}