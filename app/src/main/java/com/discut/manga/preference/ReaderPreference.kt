package com.discut.manga.preference

import android.content.SharedPreferences
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import discut.manga.common.res.R
import kotlinx.coroutines.flow.Flow
import manga.core.preference.AppPreference

class ReaderPreference(appPreference: SharedPreferences, flow: Flow<String?>) :
    AppPreference(appPreference, flow) {

    companion object {
        const val READER_MODE = "reader_mode"
    }

    var readerMode
        get() =
            ReaderMode.entries.find { it.value == getValue(READER_MODE, 0) }
                ?: ReaderMode.WEBTOON
        set(value) {
            edit { putInt(READER_MODE, value.value) }
        }
}

enum class ReaderMode(
    val value: Int,
    @StringRes val stringRes: Int,
    @DrawableRes val iconRes: Int,
) {
    WEBTOON(
        0,
        R.string.reader_mode_webtoon,
        com.discut.manga.R.drawable.ic_reader_mode_webtoon_24
    ),
    LEFT_TO_RIGHT(
        1,
        R.string.reader_mode_left_to_right,
        com.discut.manga.R.drawable.ic_reader_mode_ltr_24
    )
}