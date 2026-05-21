package com.zekaoformani.macera.data

expect class SoundManager {
    fun playJump()
    fun playCollect()
    fun playGameOver()
    fun playBackgroundMusic(musicName: String)
    fun stopBackgroundMusic()

    companion object {
        fun getInstance(): SoundManager
    }
}
