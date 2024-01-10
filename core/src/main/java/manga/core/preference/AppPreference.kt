package manga.core.preference

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

open class AppPreference constructor(
    private val appPreference: SharedPreferences,
    private val keysFlow: Flow<String?>
) {

    protected fun <T> getValue(key: String, defaultValue: T): T {
        if (defaultValue == null) {
            throw IllegalArgumentException("defaultValue cannot be null")
        }
        appPreference.all[key]?.let {
            if (it::class.java == defaultValue!!::class.java) {
                return it as T
            }
        }
        return defaultValue
    }

    protected fun <T> getValueAsFlow(key: String, defaultValue: T): Flow<T> {
        return keysFlow.filter {
            key == it
        }
            .map { getValue(key, defaultValue) }
            .conflate()
    }

    protected fun getStringSet(key: String, defaultValue: Set<String>): Set<String> {
        return appPreference.getStringSet(key, defaultValue)!!
    }

    protected fun getEdit(): SharedPreferences.Editor {
        return appPreference.edit()
    }

    protected fun edit(block: SharedPreferences.Editor.() -> Unit) {
        appPreference.edit().apply {
            block()
            apply()
        }
    }

}