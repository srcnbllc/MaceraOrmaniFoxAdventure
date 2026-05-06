package com.zekaoformani.macera.data

import android.content.Context

class DataManager(context: Context) {
    private val prefs = context.getSharedPreferences("MaceraOrmaniPrefs", Context.MODE_PRIVATE)

    // --- ALTIN SİSTEMİ ---
    fun getTotalCoins(): Int = prefs.getInt("total_coins", 0)

    fun addCoins(amount: Int) {
        val current = getTotalCoins()
        prefs.edit().putInt("total_coins", current + amount).apply()
    }

    // YENİ: Karakter satın alırken altın harcama
    fun spendCoins(amount: Int): Boolean {
        val current = getTotalCoins()
        if (current >= amount) {
            prefs.edit().putInt("total_coins", current - amount).apply()
            return true // Satın alma başarılı
        }
        return false // Yetersiz bakiye
    }

    // --- SKOR SİSTEMİ ---
    fun getHighScore(): Int = prefs.getInt("high_score", 0)

    fun saveHighScore(score: Int) {
        val currentHigh = getHighScore()
        if (score > currentHigh) {
            prefs.edit().putInt("high_score", score).apply()
        }
    }

    // --- KARAKTER KİLİT SİSTEMİ (YENİ) ---
    // Tilki (ID: 1) varsayılan olarak her zaman açıktır.
    fun isCharacterUnlocked(characterId: Int): Boolean {
        if (characterId == 1) return true
        return prefs.getBoolean("unlocked_char_$characterId", false)
    }

    // Karakterin kilidini kalıcı olarak açar
    fun unlockCharacter(characterId: Int) {
        prefs.edit().putBoolean("unlocked_char_$characterId", true).apply()
    }
}