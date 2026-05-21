package com.zekaoformani.macera

import androidx.compose.runtime.Composable

expect fun showToast(message: String)

@Composable
expect fun enablePlatformEdgeToEdge(darkTheme: Boolean)
