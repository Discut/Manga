package manga.core.preference

import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow

class HistoryPreference constructor(appPreference: SharedPreferences, flow: Flow<String?>) :
    AppPreference(appPreference, flow) {
    fun getHistoryListLayoutAsFlow(): Flow<Int> {

        return getValueAsFlow("history_list_layout", 0)
    }

    fun getHistoryListLayout(): Int {
        return getValue("history_list_layout", 0)
    }

    fun setHistoryListLayout(value: Int) {
        getEdit().putInt("history_list_layout", value).apply()
    }
}