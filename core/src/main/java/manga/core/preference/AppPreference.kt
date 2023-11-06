package manga.core.preference

import android.content.SharedPreferences

open class AppPreference constructor(private val appPreference: SharedPreferences) {
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

    protected fun getStringSet(key: String, defaultValue: Set<String>): Set<String> {
        return appPreference.getStringSet(key, defaultValue)!!
    }

    private fun getEdit(): SharedPreferences.Editor {
        return appPreference.edit()
    }

}