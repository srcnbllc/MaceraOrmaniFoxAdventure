package com.zekaoformani.macera

import android.app.Application
import android.os.Build
import coil.ImageLoader
import android.content.Context
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.zekaoformani.macera.data.GamePreferences

class MaceraApp : Application(), ImageLoaderFactory {

    lateinit var preferences: GamePreferences
        private set

    override fun onCreate() {
        super.onCreate()
        preferences = GamePreferences(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
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
