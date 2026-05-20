package com.zekaoformani.macera.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GamePreferences(private val settings: Settings = Settings()) {

    private val _selectedCharacterFlow = MutableStateFlow(settings.getInt("selected_character", 1))
    val selectedCharacterFlow: StateFlow<Int> = _selectedCharacterFlow.asStateFlow()

    private val _unlockedChaptersFlow = MutableStateFlow(getUnlockedChaptersList())
    val unlockedChaptersFlow: StateFlow<List<Int>> = _unlockedChaptersFlow.asStateFlow()

    private val _totalScoreFlow = MutableStateFlow(settings.getInt("total_score", 0))
    val totalScoreFlow: StateFlow<Int> = _totalScoreFlow.asStateFlow()

    // Tutorial gösterildi mi?
    fun isTutorialShown(): Boolean = settings.getBoolean("tutorial_shown", false)
    fun setTutorialShown(shown: Boolean) {
        settings["tutorial_shown"] = shown
    }

    // Müzik & SFX ayarları
    fun isMusicEnabled(): Boolean = settings.getBoolean("music_enabled", true)
    fun setMusicEnabled(enabled: Boolean) {
        settings["music_enabled"] = enabled
    }

    fun isSfxEnabled(): Boolean = settings.getBoolean("sfx_enabled", true)
    fun setSfxEnabled(enabled: Boolean) {
        settings["sfx_enabled"] = enabled
    }

    fun getUnlockedChapter(): Int {
        return settings.getInt("unlocked_chapter", 1)
    }

    fun setUnlockedChapter(chapter: Int) {
        settings["unlocked_chapter"] = chapter
        _unlockedChaptersFlow.value = getUnlockedChaptersList()
    }

    fun setSelectedCharacter(charId: Int) {
        settings["selected_character"] = charId
        _selectedCharacterFlow.value = charId
    }

    fun updateScore(points: Int) {
        val current = settings.getInt("total_score", 0)
        val newScore = current + points
        settings["total_score"] = newScore
        _totalScoreFlow.value = newScore
    }

    private fun getUnlockedChaptersList(): List<Int> {
        val max = getUnlockedChapter()
        return (1..max).toList()
    }
}
