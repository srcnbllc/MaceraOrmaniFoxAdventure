package com.zekaoformani.macera

import androidx.compose.runtime.Composable

@Composable
actual fun enablePlatformEdgeToEdge(darkTheme: Boolean) {
    // iOS'ta varsayılan olarak edge-to-edge açıktır. 
    // İhtiyaç duyulursa burada status bar rengi ayarlanabilir.
}
