package photos.rchugunov.com

import android.content.SharedPreferences

class TokenProvider(
    private val sharedPreferences: SharedPreferences
) {
    fun getToken(): String? = sharedPreferences.getString(AUTH_TOKEN_KEY, null)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(AUTH_TOKEN_KEY, token).apply()
    }

    fun getRefreshToken(): String? = sharedPreferences.getString(AUTH_REFRESH_TOKEN_KEY, null)

    fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString(AUTH_REFRESH_TOKEN_KEY, token).apply()
    }

    fun clear() {
        sharedPreferences.edit().remove(AUTH_TOKEN_KEY).apply()
        sharedPreferences.edit().remove(AUTH_REFRESH_TOKEN_KEY).apply()
    }

    companion object {
        const val AUTH_PREFS = "AUTH_PREFS"
        private const val AUTH_TOKEN_KEY = "AUTH_TOKEN_KEY"
        private const val AUTH_REFRESH_TOKEN_KEY = "AUTH_REFRESH_TOKEN_KEY"
    }
}