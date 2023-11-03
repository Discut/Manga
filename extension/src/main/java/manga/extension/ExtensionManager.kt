package manga.extension

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtensionManager @Inject constructor() {

    fun getString(): String {
        return "ExtensionManager"
    }
}