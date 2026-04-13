package com.zekaoformani.macera

import android.app.Application
import com.zekaoformani.macera.data.GamePreferences

class MaceraApp : Application() {

    lateinit var preferences: GamePreferences
        private set

    override fun onCreate() {
        super.onCreate()
        preferences = GamePreferences(this)
    }

    companion object {
        const val TAG = "MaceraOrmani"
    }
}
