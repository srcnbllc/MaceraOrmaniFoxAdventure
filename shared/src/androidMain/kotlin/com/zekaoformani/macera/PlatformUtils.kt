package com.zekaoformani.macera

import android.widget.Toast

actual fun showToast(message: String) {
    Toast.makeText(GlobalContext.get(), message, Toast.LENGTH_SHORT).show()
}
