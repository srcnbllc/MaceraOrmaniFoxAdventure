package com.zekaoformani.macera.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.zekaoformani.macera.R

class SoundManager private constructor(val context: Context) {
    private val gamePrefs = GamePreferences(context)
    private var soundPool: SoundPool
    private var jumpSoundId: Int = 0
    private var collectSoundId: Int = 0
    private var gameOverSoundId: Int = 0
    private var bgMusicPlayer: MediaPlayer? = null
    private var currentMusicResId: Int = -1

    companion object {
        @Volatile
        private var INSTANCE: SoundManager? = null
        fun getInstance(context: Context): SoundManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SoundManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        soundPool = SoundPool.Builder().setMaxStreams(5).setAudioAttributes(audioAttributes).build()
        jumpSoundId = soundPool.load(context, R.raw.jump, 1)
        collectSoundId = soundPool.load(context, R.raw.collect, 1)
        gameOverSoundId = soundPool.load(context, R.raw.game_over, 1)
    }

    fun playJump() { if (gamePrefs.isSfxEnabled()) soundPool.play(jumpSoundId, 1f, 1f, 0, 0, 1f) }
    fun playCollect() { if (gamePrefs.isSfxEnabled()) soundPool.play(collectSoundId, 0.1f, 0.1f, 0, 0, 1f) }
    fun playGameOver() { if (gamePrefs.isSfxEnabled()) soundPool.play(gameOverSoundId, 1f, 1f, 0, 0, 1f) }

    fun playBackgroundMusic(musicResId: Int) {
        if (!gamePrefs.isMusicEnabled()) { stopBackgroundMusic(); return }
        if (currentMusicResId == musicResId && bgMusicPlayer?.isPlaying == true) return
        stopBackgroundMusic()
        try {
            bgMusicPlayer = MediaPlayer.create(context, musicResId).apply {
                isLooping = true
                setVolume(0.4f, 0.4f)
                start()
            }
            currentMusicResId = musicResId
        } catch (e: Exception) { e.printStackTrace() }
    }

    fun stopBackgroundMusic() {
        bgMusicPlayer?.apply { if (isPlaying) stop(); release() }
        bgMusicPlayer = null
        currentMusicResId = -1
    }
}