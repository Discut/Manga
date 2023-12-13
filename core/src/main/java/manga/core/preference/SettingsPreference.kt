package manga.core.preference

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow

class SettingsPreference constructor(appPreference: SharedPreferences) :
    AppPreference(appPreference) {
    fun isDarkMode(): Boolean {
        return getValue("dark_mode", false)
    }

    fun enableSecurityMode(): Boolean {
        return getValue("enable_security_mode", false)
    }

    fun setSecurityMode(enable: Boolean) {
        getEdit().putBoolean("enable_security_mode", enable).apply()
    }

    fun getSecurityModeAsFlow(): Flow<Boolean> {
        return getValueAsFlow("enable_security_mode", false)
    }
}