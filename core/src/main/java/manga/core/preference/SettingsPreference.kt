package manga.core.preference

import android.content.SharedPreferences

class SettingsPreference constructor(appPreference: SharedPreferences) :
    AppPreference(appPreference) {
    fun isDarkMode(): Boolean {
        return getValue("dark_mode", false)
    }
}