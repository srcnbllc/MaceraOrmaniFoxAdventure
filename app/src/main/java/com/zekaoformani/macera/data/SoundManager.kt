package com.zekaoformani.macera.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.zekaoformani.macera.R

class SoundManager(val context: Context) {

    // DÜZELTİLDİ: Ayarlar DataManager'da değil, GamePreferences içindeydi!
    private val gamePrefs = GamePreferences(context)

    // --- SES EFEKTLERİ İÇİN SOUNDPOOL ---
    private var soundPool: SoundPool
    private var jumpSoundId: Int = 0
    private var collectSoundId: Int = 0
    private var gameOverSoundId: Int = 0

    // --- ARKA PLAN MÜZİĞİ İÇİN MEDIAPLAYER ---
    private var bgMusicPlayer: MediaPlayer? = null

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Efekt dosyalarını yüklüyoruz.
        jumpSoundId = soundPool.load(context, R.raw.jump, 1)
        collectSoundId = soundPool.load(context, R.raw.collect, 1)
        gameOverSoundId = soundPool.load(context, R.raw.game_over, 1)
    }

    // --- SES EFEKTİ ÇALMA FONKSİYONLARI ---
    fun playJump() {
        if (gamePrefs.isSfxEnabled()) { // Düzeltildi
            soundPool.play(jumpSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playCollect() {
        if (gamePrefs.isSfxEnabled()) { // Düzeltildi
            soundPool.play(collectSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playGameOver() {
        if (gamePrefs.isSfxEnabled()) { // Düzeltildi
            soundPool.play(gameOverSoundId, 1f, 1f, 0, 0, 1f)
        }
    }

    // --- ARKA PLAN MÜZİĞİ FONKSİYONLARI ---
    fun playBackgroundMusic() {
        if (!gamePrefs.isMusicEnabled()) { // Düzeltildi
            pauseBackgroundMusic()
            return
        }

        if (bgMusicPlayer == null) {
            bgMusicPlayer = MediaPlayer.create(context, R.raw.orman_muzigi)
            bgMusicPlayer?.isLooping = true
            bgMusicPlayer?.setVolume(0.4f, 0.4f)
        }

        if (bgMusicPlayer?.isPlaying == false) {
            bgMusicPlayer?.start()
        }
    }

    fun pauseBackgroundMusic() {
        if (bgMusicPlayer?.isPlaying == true) {
            bgMusicPlayer?.pause()
        }
    }

    fun release() {
        soundPool.release()
        bgMusicPlayer?.stop()
        bgMusicPlayer?.release()
        bgMusicPlayer = null
    }
}