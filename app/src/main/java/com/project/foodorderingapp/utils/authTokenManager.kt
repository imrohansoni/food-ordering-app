package com.project.foodorderingapp.utils

import android.content.Context
import android.content.SharedPreferences

object AuthTokenManager {
    private const val AUTH_PREFS_NAME = "auth_prefs"
    private const val AUTH_TOKEN_KEY = "auth_token"

    // Function to save the authentication token
    fun saveAuthToken(context: Context, authToken: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            AUTH_PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(AUTH_TOKEN_KEY, authToken)
        editor.apply()
    }

    fun removeAuthToken(context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            AUTH_PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.remove(AUTH_TOKEN_KEY)
        editor.apply()
    }

    fun getAuthToken(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            AUTH_PREFS_NAME,
            Context.MODE_PRIVATE
        )
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null)
    }
}
