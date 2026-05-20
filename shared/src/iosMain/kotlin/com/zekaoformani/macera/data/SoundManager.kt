package com.zekaoformani.macera.data

actual class SoundManager {
    actual fun playJump() {}
    actual fun playCollect() {}
    actual fun playGameOver() {}
    actual fun playBackgroundMusic(musicName: String) {}
    actual fun stopBackgroundMusic() {}

    actual companion object {
        private val instance = SoundManager()
        actual fun getInstance(): SoundManager = instance
    }
}
