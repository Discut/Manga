package manga.core.preference

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow

class ReaderPreference constructor(appPreference: SharedPreferences, flow: Flow<String?>) :
    AppPreference(appPreference, flow) {

}