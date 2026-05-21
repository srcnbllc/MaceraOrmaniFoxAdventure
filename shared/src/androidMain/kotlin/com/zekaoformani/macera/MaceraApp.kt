package com.zekaoformani.macera

import android.app.Application
import android.os.Build
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.gif.GifDecoder
import coil3.gif.AnimatedImageDecoder
import com.zekaoformani.macera.data.GamePreferences
import com.zekaoformani.macera.data.SoundManager

class MaceraApp : Application(), SingletonImageLoader.Factory {

    lateinit var preferences: GamePreferences
        private set

    override fun onCreate() {
        super.onCreate()
        GlobalContext.init(this)
        preferences = GamePreferences()
        SoundManager.init(this)
    }

    override fun newImageLoader(context: coil3.PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(AnimatedImageDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    companion object {
        const val TAG = "MaceraOrmani"
    }
}
