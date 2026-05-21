package com.zekaoformani.macera.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.content.res.AssetFileDescriptor

actual class SoundManager(private val context: Context) {
    private val gamePrefs = GamePreferences()
    private val soundPool: SoundPool
    private val jumpSoundId: Int
    private val collectSoundId: Int
    private val gameOverSoundId: Int
    private var bgMusicPlayer: MediaPlayer? = null
    private var currentMusicName: String? = null

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()
        soundPool = SoundPool.Builder().setMaxStreams(5).setAudioAttributes(audioAttributes).build()
        
        // KMP resources are in assets/composeResources/files/
        jumpSoundId = loadSound("jump.wav")
        collectSoundId = loadSound("collect.wav")
        gameOverSoundId = loadSound("game_over.wav")
    }

    private fun loadSound(fileName: String): Int {
        return try {
            val afd = context.assets.openFd("composeResources/maceraormanifoxadventure.shared.generated.resources/files/$fileName")
            soundPool.load(afd, 1)
        } catch (e: Exception) {
            0
        }
    }

    actual fun playJump() { if (gamePrefs.isSfxEnabled()) soundPool.play(jumpSoundId, 1f, 1f, 0, 0, 1f) }
    actual fun playCollect() { if (gamePrefs.isSfxEnabled()) soundPool.play(collectSoundId, 0.1f, 0.1f, 0, 0, 1f) }
    actual fun playGameOver() { if (gamePrefs.isSfxEnabled()) soundPool.play(gameOverSoundId, 1f, 1f, 0, 0, 1f) }

    actual fun playBackgroundMusic(musicName: String) {
        if (!gamePrefs.isMusicEnabled()) { stopBackgroundMusic(); return }
        if (currentMusicName == musicName && bgMusicPlayer?.isPlaying == true) return
        stopBackgroundMusic()
        try {
            val afd = context.assets.openFd("composeResources/maceraormanifoxadventure.shared.generated.resources/files/$musicName.wav")
            bgMusicPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                isLooping = true
                setVolume(0.4f, 0.4f)
                prepare()
                start()
            }
            currentMusicName = musicName
        } catch (e: Exception) { e.printStackTrace() }
    }

    actual fun stopBackgroundMusic() {
        bgMusicPlayer?.apply { if (isPlaying) stop(); release() }
        bgMusicPlayer = null
        currentMusicName = null
    }

    actual companion object {
        private var instance: SoundManager? = null
        private lateinit var appContext: Context

        fun init(context: Context) {
            appContext = context.applicationContext
        }

        actual fun getInstance(): SoundManager {
            return instance ?: SoundManager(appContext).also { instance = it }
        }
    }
}
