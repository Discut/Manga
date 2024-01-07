package manga.core.preference

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow

class SettingsPreference constructor(appPreference: SharedPreferences, flow: Flow<String?>) :
    AppPreference(appPreference, flow) {
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

    fun setNoTranceMode(enable: Boolean) {
        getEdit().putBoolean("enable_no_trance_mode", enable).apply()
    }

    fun getNoTranceModeAsFlow(enable: Boolean = false): Flow<Boolean> {
        return getValueAsFlow("enable_no_trance_mode", enable)
    }

    fun getNoTranceMode(): Boolean {
        return getValue("enable_no_trance_mode", false)
    }


}