package com.example.aguiabrancachallenge.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(
            "user_prefs",
            Context.MODE_PRIVATE
        )

    fun saveLogin(
        matricula: String,
        senha: String
    ) {

        prefs.edit {
            putString("matricula", matricula)
                .putString("password", senha)
                .putBoolean("is_logged", true)
        }
    }

    fun getMatricula(): String? {
        return prefs.getString("matricula", null)
    }

    fun getPassword(): String? {
        return prefs.getString("password", null)
    }

    fun isLogged(): Boolean {
        return prefs.getBoolean("is_logged", false)
    }

    fun logout() {
        prefs.edit()
            .clear()
            .apply()
    }
}