package com.ojiem.yggdrasil.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class SettingsViewModel(context: Context) : ViewModel() {
    private val prefs = context.getSharedPreferences("yggdrasil_prefs", Context.MODE_PRIVATE)
    
    var isMusicEnabled = mutableStateOf(prefs.getBoolean("music_enabled", true))
        private set

    fun toggleMusic(enabled: Boolean) {
        isMusicEnabled.value = enabled
        prefs.edit().putBoolean("music_enabled", enabled).apply()
    }
}
