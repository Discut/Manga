package managa.source

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceScreen
import manga.core.application.application

/**
 * source config
 */
interface ConfigurationSource : Source {
    fun getPreferences(): SharedPreferences =
        application!!.getSharedPreferences("manga_source_$id", Context.MODE_PRIVATE)
    /*getContext().getSharedPreferences("manga_source_$id", Context.MODE_PRIVATE)*/

    fun setPreferenceScreen(screen: PreferenceScreen)
}