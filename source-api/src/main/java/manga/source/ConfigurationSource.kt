package manga.source

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceScreen
import manga.core.application.application
import manga.source.preference.SourcePreference
import manga.source.preference.SourcePreferenceImpl
import manga.source.preference.SourcePreferenceType

/**
 * source config
 */
interface ConfigurationSource : BaseSource {
    fun getPreferences(): SharedPreferences =
        application!!.getSharedPreferences("manga_source_$id", Context.MODE_PRIVATE)
    /*getContext().getSharedPreferences("manga_source_$id", Context.MODE_PRIVATE)*/

    @Deprecated("use setPreferenceScreen(builder: (LazyListScope.() -> Unit) -> Unit)")
    fun setPreferenceScreen(screen: PreferenceScreen) {/*Deprecated*/
    }

    fun getSourcePreferences():List<SourcePreferenceType<*>>{
        return SourcePreferenceImpl().apply {
            setPreferenceScreen()
        }.preferences.toList()
    }
    fun SourcePreference.setPreferenceScreen()

}