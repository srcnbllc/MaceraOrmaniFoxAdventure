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

    // Karakter satın alırken altın harcama
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

    // --- SON 10 SKOR SİSTEMİ (YENİ EKLENDİ) ---
    fun getLastScores(): List<Int> {
        val scoreStr = prefs.getString("last_scores", "") ?: ""
        if (scoreStr.isEmpty()) return emptyList()
        return scoreStr.split(",").mapNotNull { it.toIntOrNull() }
    }

    fun saveLastScore(score: Int) {
        val scores = getLastScores().toMutableList()
        scores.add(0, score) // Yeni skoru listenin en başına ekle
        val limitedScores = scores.take(10) // Sadece son 10 skoru tut
        prefs.edit().putString("last_scores", limitedScores.joinToString(",")).apply()
    }

    // --- KARAKTER KİLİT SİSTEMİ ---
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