package com.zekaoformani.macera.data

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.zekaoformani.macera.R

class SoundManager(context: Context) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        ).build()

    private val jumpSoundId = soundPool.load(context, R.raw.jump, 1)
    private val collectSoundId = soundPool.load(context, R.raw.collect, 1)
    private val gameOverSoundId = soundPool.load(context, R.raw.game_over, 1)

    private var sfxEnabled = true

    fun setSfxEnabled(enabled: Boolean) {
        sfxEnabled = enabled
    }

    fun playJump() {
        if (sfxEnabled) soundPool.play(jumpSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun playCollect() {
        if (sfxEnabled) soundPool.play(collectSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun playGameOver() {
        if (sfxEnabled) soundPool.play(gameOverSoundId, 1f, 1f, 0, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
