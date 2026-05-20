package com.zekaoformani.macera

import android.content.Context

object GlobalContext {
    private var context: Context? = null

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    fun get(): Context {
        return context ?: throw IllegalStateException("GlobalContext not initialized")
    }
}
