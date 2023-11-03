package managa.source

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceScreen

/**
 * source config
 */
interface ConfigurationSource : Source {
    fun getPreferences(): SharedPreferences =
        getContext().getSharedPreferences("manga_source_$id", Context.MODE_PRIVATE)

    fun getContext(): Context
    fun setPreferenceScreen(screen: PreferenceScreen)
}