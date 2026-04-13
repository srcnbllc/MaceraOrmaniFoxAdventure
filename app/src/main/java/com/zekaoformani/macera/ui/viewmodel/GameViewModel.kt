package com.zekaoformani.macera.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zekaoformani.macera.data.GamePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(private val preferences: GamePreferences) : ViewModel() {

    val selectedCharacter = preferences.selectedCharacterFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 1
    )

    val unlockedChapters = preferences.unlockedChaptersFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(1)
    )
    
    val totalScore = preferences.totalScoreFlow.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    fun selectCharacter(id: Int) {
        viewModelScope.launch {
            preferences.setSelectedCharacter(id)
        }
    }

    fun unlockChapter(id: Int) {
        viewModelScope.launch {
            preferences.unlockChapter(id)
        }
    }

    fun addScore(points: Int) {
        viewModelScope.launch {
            preferences.updateScore(points)
        }
    }
}

class GameViewModelFactory(private val preferences: GamePreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(preferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
