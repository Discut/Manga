package manga.core.preference

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import manga.core.base.BaseManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager @Inject constructor(@ApplicationContext private val context: Context) :
    BaseManager {
    lateinit var appPreference: SharedPreferences
    val preferencesMap = mutableMapOf<String, AppPreference>()

    lateinit var keysFlow: Flow<String?>
    override fun initManager() {
        this.appPreference =
            androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        this.keysFlow = callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key: String? ->
                trySend(
                    key,
                )
            }
            appPreference.registerOnSharedPreferenceChangeListener(listener)
            awaitClose {
                appPreference.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    inline fun <reified T : AppPreference> getPreferences(): T {
        preferencesMap[T::class.java.simpleName]?.let {
            if (it is T) {
                return it
            }
        }
        T::class.constructors.forEach {
            if (it.parameters.size == 2 && it.parameters[0].type.classifier == SharedPreferences::class) {
                val preference = it.call(appPreference, keysFlow)
                preferencesMap[T::class.java.simpleName] = preference
                return@getPreferences preference
            }
        }
        throw IllegalArgumentException("${T::class.java.simpleName} not found")
    }

    companion object

}
