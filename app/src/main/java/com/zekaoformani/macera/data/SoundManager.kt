package com.zekaoformani.macera.data

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import com.zekaoformani.macera.R

class SoundManager(val context: Context) {

    // --- SES EFEKTLERİ İÇİN SOUNDPOOL (Kısa Sesler: Zıplama, Altın Alma vb.) ---
    private var soundPool: SoundPool
    private var jumpSoundId: Int = 0
    private var collectSoundId: Int = 0
    private var gameOverSoundId: Int = 0

    // --- ARKA PLAN MÜZİĞİ İÇİN MEDIAPLAYER (Uzun Müzikler) ---
    private var bgMusicPlayer: MediaPlayer? = null

    init {
        // Android'e bu seslerin oyun için olduğunu söylüyoruz
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5) // Aynı anda en fazla kaç ses efekti üst üste çalabilir
            .setAudioAttributes(audioAttributes)
            .build()

        // Efekt dosyalarını yüklüyoruz.
        // NOT: Eğer "jump", "collect", "gameover" dosyalarının adları senin raw klasöründe farklıysa buraları düzeltmeyi unutma!
        jumpSoundId = soundPool.load(context, R.raw.jump, 1)
        collectSoundId = soundPool.load(context, R.raw.collect, 1)
        gameOverSoundId = soundPool.load(context, R.raw.game_over, 1)
    }

    // --- SES EFEKTİ ÇALMA FONKSİYONLARI (GameScreen'de kullandıkların) ---
    fun playJump() {
        soundPool.play(jumpSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun playCollect() {
        soundPool.play(collectSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun playGameOver() {
        soundPool.play(gameOverSoundId, 1f, 1f, 0, 0, 1f)
    }

    // --- ARKA PLAN MÜZİĞİ FONKSİYONLARI ---
    fun playBackgroundMusic() {
        if (bgMusicPlayer == null) {
            bgMusicPlayer = MediaPlayer.create(context, R.raw.orman_muzigi)
            bgMusicPlayer?.isLooping = true // Müziğin başa sarıp sonsuza kadar çalmasını sağlar
            bgMusicPlayer?.setVolume(0.4f, 0.4f) // Müzik sesi (0.0 en kısık, 1.0 en yüksek). Efektleri bastırmaması için 0.4 iyidir.
        }
        bgMusicPlayer?.start()
    }

    fun pauseBackgroundMusic() {
        if (bgMusicPlayer?.isPlaying == true) {
            bgMusicPlayer?.pause()
        }
    }

    // --- HAFIZAYI TEMİZLEME (Ekran veya Oyun Kapanınca RAM'i boşaltır) ---
    fun release() {
        soundPool.release()

        bgMusicPlayer?.stop()
        bgMusicPlayer?.release()
        bgMusicPlayer = null
    }
}