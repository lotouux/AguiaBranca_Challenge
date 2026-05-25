package com.example.aguiabrancachallenge.data.preferences

import android.content.Context
import androidx.core.content.edit

class ThemePreferences(context: Context) {

    private val sharedPreferences =
        context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    fun saveDarkMode(isDark: Boolean) {
        sharedPreferences.edit { putBoolean("dark_mode", isDark) }
    }

    fun isDarkMode(): Boolean {
        return sharedPreferences.getBoolean("dark_mode", true)
    }
}