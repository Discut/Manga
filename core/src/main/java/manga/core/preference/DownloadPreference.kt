package manga.core.preference

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow

class DownloadPreference constructor(appPreference: SharedPreferences, flow: Flow<String?>) :
    AppPreference(appPreference, flow) {

    fun isWifiOnly(): Boolean =
        getValue("enable_wifi_only", false)

    fun getIsWifiOnlyAsFlow(): Flow<Boolean> =
        getValueAsFlow("enable_wifi_only", false)


    fun setWifiOnly(enable: Boolean) {
        edit {
            putBoolean("enable_wifi_only", enable)
        }
    }
}