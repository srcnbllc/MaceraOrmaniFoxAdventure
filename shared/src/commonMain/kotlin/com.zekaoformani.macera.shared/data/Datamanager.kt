package com.zekaoformani.macera.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class DataManager(private val settings: Settings = Settings()) {

    // --- ALTIN SİSTEMİ ---
    fun getTotalCoins(): Int = settings.getInt("total_coins", 0)

    fun addCoins(amount: Int) {
        val current = getTotalCoins()
        settings["total_coins"] = current + amount
    }

    // Karakter satın alırken altın harcama
    fun spendCoins(amount: Int): Boolean {
        val current = getTotalCoins()
        if (current >= amount) {
            settings["total_coins"] = current - amount
            return true
        }
        return false
    }

    // --- YENİ KAMP SİSTEMİ İÇİN HARCAMA FONKSİYONU ---
    fun spendCoins(gamePrefs: GamePreferences, amount: Int): Boolean {
        val currentCoins = gamePrefs.totalScoreFlow.value
        if (currentCoins >= amount) {
            gamePrefs.updateScore(-amount)
            return true
        }
        return false
    }

    // --- BadgePoolScreen için gerekenler ---
    fun getHighScore(): Int = settings.getInt("high_score", 0)

    fun isCharacterUnlocked(charId: Int): Boolean {
        if (charId == 1) return true
        return settings.getBoolean("char_unlocked_$charId", false)
    }
}
